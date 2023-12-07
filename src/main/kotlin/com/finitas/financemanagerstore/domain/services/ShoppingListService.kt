package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.ShoppingListDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.domain.model.ShoppingList
import com.finitas.financemanagerstore.domain.repositories.ShoppingListRepository
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class ShoppingListService(private val repository: ShoppingListRepository) {

    @Transactional
    fun addShoppingList(dto: ShoppingListDto): ShoppingListDto {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val entity = dto.toEntity(newItemVersion, UUID.randomUUID().toString())
        repository.save(entity)
        return ShoppingListDto.fromEntity(entity)
    }

    @Transactional
    fun updateShoppingList(dto: ShoppingListDto): ShoppingListDto {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val oldEntity = repository.findByIdUserAndIdShoppingList(dto.idUser, dto.idShoppingList)
            ?: throw Exception("Shopping list not found")
        val entity = dto.toEntity(newItemVersion, oldEntity.internalId)
        repository.save(entity)
        return ShoppingListDto.fromEntity(entity)
    }

    private fun isDeletedOnServerAndUpdatedOnClient(
        dto: ShoppingListDto,
        entity: ShoppingList
    ) =
        dto.isDeleted != 1 && entity.isDeleted == 1

    private fun getMaxVersionFromDb(userId: String): Int {
        return repository.findByIdUser(
            userId,
            Sort.by(Sort.Direction.DESC, "version"),
            Limit.of(1)
        ).firstOrNull()?.version ?: 0
    }

    @Transactional
    fun synchronize(dto: SynchronizationRequest<ShoppingListDto>): SynchronizationResponse<ShoppingListDto> {
        val userId = dto.objects.firstOrNull()?.idUser ?: throw Exception("No data to update provided")

        val itemsChangedAfterLastSync = repository.findAllByIdUserAndVersionGreaterThan(userId, dto.lastSyncVersion)
        val serverChangedItemsAssociatedByIds = itemsChangedAfterLastSync.associateBy { it.idShoppingList }
        val versionCounter = AtomicInteger(getMaxVersionFromDb(userId))

        val clientChangesToSaveToDb = dto.objects.filter {
            val entityFromServer = serverChangedItemsAssociatedByIds[it.idShoppingList]
            entityFromServer == null || (dto.isAuthorDataToUpdate && isDeletedOnServerAndUpdatedOnClient(
                it,
                entityFromServer
            ))
        }.map { it.toEntity(versionCounter.incrementAndGet(), UUID.randomUUID().toString()) }

        repository.saveAll(clientChangesToSaveToDb)

        return SynchronizationResponse(
            actualizedSyncVersion = versionCounter.get(),
            objects = repository
                .findAllByIdUserAndVersionGreaterThan(userId, dto.lastSyncVersion)
                .map { ShoppingListDto.fromEntity(it) }
        )
    }
}