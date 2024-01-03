package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.ShoppingListDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.config.ConflictException
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.ShoppingList
import com.finitas.financemanagerstore.domain.repositories.ShoppingListRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Component
class ShoppingListService(
    private val repository: ShoppingListRepository,
    private val mongoTemplate: MongoTemplate
) {

    private fun isDeletedOnServerAndUpdatedOnClient(
        dto: ShoppingListDto,
        entity: ShoppingList
    ) =
        !dto.isDeleted && entity.isDeleted

    private fun getMaxVersionFromDb(userId: UUID): Int {
        return repository.findByIdUser(
            userId,
            Sort.by(Sort.Direction.DESC, "version"),
            Limit.of(1)
        ).firstOrNull()?.version ?: 0
    }

    fun getAll(idUser: UUID): List<ShoppingListDto> {
        return repository.findAllByIdUser(idUser)
            .map { ShoppingListDto.fromEntity(it) }
    }

    @Transactional
    fun insert(dto: ShoppingListDto): Int {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        try {
            repository.save(dto.toEntity(newItemVersion, UUID.randomUUID()))
        } catch (_: DuplicateKeyException) {
            throw ConflictException(ErrorCode.SHOPPING_LIST_EXISTS, "Shopping list already exists")
        }

        return newItemVersion
    }

    @Transactional
    fun update(dto: ShoppingListDto): Int {
        val entity = repository.findByIdUserAndIdShoppingList(dto.idUser, dto.idShoppingList)
            ?: throw NotFoundException(ErrorCode.SHOPPING_LIST_NOT_FOUND, "Shopping list not found")

        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val query = Query(
            Criteria.where("internalId").`is`(entity.internalId)
        )
        val update = Update()
            .set("version", newItemVersion)
            .set("name", dto.name)
            .set("color", dto.color)
            .set("isDeleted", dto.isDeleted)
            .set("isFinished", dto.isFinished)
            .set("shoppingItems", dto.shoppingItems)

        mongoTemplate.upsert(query, update, ShoppingList::class.java)

        return newItemVersion
    }

    @Transactional
    fun delete(idUser: UUID, idShoppingList: UUID): Int {
        val entity = repository.findByIdUserAndIdShoppingList(idUser, idShoppingList)
            ?: throw NotFoundException(ErrorCode.SHOPPING_LIST_NOT_FOUND, "Shopping list not found")

        val newVersion = getMaxVersionFromDb(idUser) + 1
        val query = Query(
            Criteria.where("internalId").`is`(entity.internalId)
        )
        val update = Update()
            .set("version", newVersion)
            .set("isDeleted", true)

        mongoTemplate.upsert(query, update, ShoppingList::class.java)
        return newVersion
    }

    @Transactional
    fun synchronize(dto: SynchronizationRequest<ShoppingListDto>): SynchronizationResponse<ShoppingListDto> {
        val itemsChangedAfterLastSync = repository.findAllByIdUserAndVersionGreaterThan(dto.idUser, dto.lastSyncVersion)
        val serverChangedItemsAssociatedByIds = itemsChangedAfterLastSync.associateBy { it.idShoppingList }

        dto.objects
            .filter {
                val entityFromServer = serverChangedItemsAssociatedByIds[it.idShoppingList]
                entityFromServer == null || (dto.isAuthorDataToUpdate && isDeletedOnServerAndUpdatedOnClient(
                    it,
                    entityFromServer
                ))
            }
            .forEach {
                val isExists = repository.existsByIdUserAndIdShoppingList(
                    idUser = it.idUser,
                    idShoppingList = it.idShoppingList
                )

                if (isExists) update(it)
                else insert(it)
            }

        return SynchronizationResponse(
            actualizedSyncVersion = getMaxVersionFromDb(dto.idUser),
            objects = repository
                .findAllByIdUserAndVersionGreaterThan(dto.idUser, dto.lastSyncVersion)
                .map { ShoppingListDto.fromEntity(it) }
        )
    }
}