package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.FetchUpdatesResponse
import com.finitas.financemanagerstore.api.dto.IdUserWithEntities
import com.finitas.financemanagerstore.api.dto.IdUserWithVersion
import com.finitas.financemanagerstore.api.dto.ShoppingListDto
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Service
@Transactional
class ShoppingListService(
    private val repository: ShoppingListRepository,
    private val mongoTemplate: MongoTemplate
) {

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

    fun insert(dto: ShoppingListDto, newItemVersion: Int = getMaxVersionFromDb(dto.idUser) + 1): Int {
        try {
            repository.save(dto.toEntity(newItemVersion, UUID.randomUUID()))
        } catch (_: DuplicateKeyException) {
            throw ConflictException(ErrorCode.SHOPPING_LIST_EXISTS, "Shopping list already exists")
        }

        return newItemVersion
    }

    fun update(dto: ShoppingListDto, newItemVersion: Int = getMaxVersionFromDb(dto.idUser) + 1): Int {
        val entity = repository.findByIdUserAndIdShoppingList(dto.idUser, dto.idShoppingList)
            ?: throw NotFoundException(ErrorCode.SHOPPING_LIST_NOT_FOUND, "Shopping list not found")

        val query = Query(
            Criteria.where("internalId").`is`(entity.internalId)
        )
        val update = Update()
            .set(ShoppingList::version, newItemVersion)
            .set(ShoppingList::name, dto.name)
            .set(ShoppingList::color, dto.color)
            .set(ShoppingList::isDeleted, dto.isDeleted)
            .set(ShoppingList::isFinished, dto.isFinished)
            .set(ShoppingList::shoppingItems, dto.shoppingItems.map { it.toEntity() })

        mongoTemplate.upsert(query, update, ShoppingList::class.java)

        return newItemVersion
    }

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

    fun fetchUsersUpdates(request: List<IdUserWithVersion>): List<FetchUpdatesResponse<ShoppingListDto>> {
        return request.mapNotNull {
            val updates = repository
                .findAllByIdUserAndVersionGreaterThan(
                    idUser = it.idUser,
                    version = it.version
                )
                .map { entity -> ShoppingListDto.fromEntity(entity) }

            updates.maxOfOrNull { update -> update.version }?.let { max ->
                FetchUpdatesResponse(
                    updates = updates,
                    idUser = it.idUser,
                    actualVersion = max
                )
            }
        }
    }

    fun updateWithChangedItems(request: IdUserWithEntities<ShoppingListDto>) {
        val currentMaxVersion = AtomicInteger(getMaxVersionFromDb(request.idUser))
        request.changedValues
            .map { it.copy(idUser = request.idUser) }
            .forEach {
                val isExists = repository.existsByIdUserAndIdShoppingList(
                    idUser = it.idUser,
                    idShoppingList = it.idShoppingList
                )

                if (isExists) update(it, currentMaxVersion.incrementAndGet())
                else insert(it, currentMaxVersion.incrementAndGet())
            }
    }
}