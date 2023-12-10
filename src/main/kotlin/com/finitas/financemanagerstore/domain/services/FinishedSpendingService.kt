package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.FinishedSpendingDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.config.BadRequestException
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.repositories.FinishedSpendingRepository
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Component
class FinishedSpendingService(private val repository: FinishedSpendingRepository) {

    @Transactional
    fun addFinishedSpending(dto: FinishedSpendingDto): FinishedSpendingDto {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val entity = dto.toEntity(newItemVersion, UUID.randomUUID().toString())
        repository.save(entity)
        return FinishedSpendingDto.fromEntity(entity)
    }

    @Transactional
    fun updateFinishedSpending(dto: FinishedSpendingDto): FinishedSpendingDto {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val oldEntity = repository.findByIdUserAndIdSpendingSummary(dto.idUser, dto.spendingSummary.idSpendingSummary)
            ?: throw NotFoundException("Finished spending not found")
        val entity = dto.toEntity(newItemVersion, oldEntity.internalId)
        repository.save(entity)
        return FinishedSpendingDto.fromEntity(entity)
    }

    @Transactional
    fun deleteFinishedSpending(idUser: String, idSpendingSummary: String): FinishedSpendingDto {
        val entity = repository.findByIdUserAndIdSpendingSummary(idUser, idSpendingSummary)
            ?: throw NotFoundException("Finished spending not found")
        entity.isDeleted = 1
        repository.save(entity)
        return FinishedSpendingDto.fromEntity(entity)
    }

    private fun isDeletedOnServerAndUpdatedOnClient(
        dto: FinishedSpendingDto,
        entity: FinishedSpending
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
    fun synchronize(dto: SynchronizationRequest<FinishedSpendingDto>): SynchronizationResponse<FinishedSpendingDto> {
        val userId = dto.objects.firstOrNull()?.idUser ?: throw BadRequestException("No data to update provided")

        val itemsChangedAfterLastSync = repository.findAllByIdUserAndVersionGreaterThan(userId, dto.lastSyncVersion)
        val serverChangedItemsAssociatedByIds = itemsChangedAfterLastSync.associateBy { it.idSpendingSummary }
        val versionCounter = AtomicInteger(getMaxVersionFromDb(userId))

        val clientChangesToSaveToDb = dto.objects.filter {
            val entityFromServer = serverChangedItemsAssociatedByIds[it.spendingSummary.idSpendingSummary]
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
                .map { FinishedSpendingDto.fromEntity(it) }
        )
    }
}