package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.FinishedSpendingDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.config.ConflictException
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.repositories.FinishedSpendingRepository
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
class FinishedSpendingService(
    private val repository: FinishedSpendingRepository,
    private val mongoTemplate: MongoTemplate
) {

    private fun isDeletedOnServerAndUpdatedOnClient(
        dto: FinishedSpendingDto,
        entity: FinishedSpending
    ) =
        !dto.isDeleted && entity.isDeleted

    private fun getMaxVersionFromDb(userId: UUID): Int {
        return repository.findByIdUser(
            userId,
            Sort.by(Sort.Direction.DESC, "version"),
            Limit.of(1)
        ).firstOrNull()?.version ?: 0
    }

    fun getAll(idUser: UUID): List<FinishedSpendingDto> {
        return repository.findAllByIdUser(idUser)
            .map { FinishedSpendingDto.fromEntity(it) }
    }

    @Transactional
    fun insert(dto: FinishedSpendingDto): Int {
        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        try {
            repository.save(dto.toEntity(newItemVersion, UUID.randomUUID()))
        } catch (_: DuplicateKeyException) {
            throw ConflictException(ErrorCode.FINISHED_SPENDING_NOT_FOUND, "Finished spending already exists")
        }

        return newItemVersion
    }

    @Transactional
    fun update(dto: FinishedSpendingDto): Int {
        val entity = repository.findByIdUserAndIdSpendingSummary(
            dto.idUser,
            dto.idSpendingSummary
        )
            ?: throw NotFoundException(ErrorCode.FINISHED_SPENDING_EXISTS, "Finished spending not found")

        val newItemVersion = getMaxVersionFromDb(dto.idUser) + 1
        val query = Query(
            Criteria.where("internalId").`is`(entity.internalId)
        )
        val update = Update()
            .set("version", newItemVersion)
            .set("isDeleted", dto.isDeleted)
            .set("idReceipt", dto.idReceipt)
            .set("spendingSummary", dto)
            .set("purchaseDate", dto.purchaseDate)

        mongoTemplate.upsert(query, update, FinishedSpending::class.java)

        return newItemVersion
    }

    @Transactional
    fun delete(idUser: UUID, idSpendingSummary: UUID): Int {
        val entity = repository.findByIdUserAndIdSpendingSummary(idUser, idSpendingSummary)
            ?: throw NotFoundException(ErrorCode.FINISHED_SPENDING_NOT_FOUND, "Finished spending not found")

        val newVersion = getMaxVersionFromDb(idUser) + 1
        val query = Query(
            Criteria.where("internalId").`is`(entity.internalId)
        )
        val update = Update()
            .set("version", newVersion)
            .set("isDeleted", true)

        mongoTemplate.upsert(query, update, FinishedSpending::class.java)
        return newVersion
    }

    @Transactional
    fun synchronize(dto: SynchronizationRequest<FinishedSpendingDto>): SynchronizationResponse<FinishedSpendingDto> {
        val itemsChangedAfterLastSync = repository.findAllByIdUserAndVersionGreaterThan(dto.idUser, dto.lastSyncVersion)
        val serverChangedItemsAssociatedByIds =
            itemsChangedAfterLastSync.associateBy { it.idSpendingSummary }

        dto.objects
            .filter {
                val entityFromServer = serverChangedItemsAssociatedByIds[it.idSpendingSummary]
                entityFromServer == null || (dto.isAuthorDataToUpdate && isDeletedOnServerAndUpdatedOnClient(
                    it,
                    entityFromServer
                ))
            }
            .forEach {
                val isExists = repository.existsByIdUserAndIdSpendingSummary(
                    idUser = it.idUser,
                    idSpendingSummary = it.idSpendingSummary
                )

                if (isExists) update(it)
                else insert(it)
            }

        return SynchronizationResponse(
            actualizedSyncVersion = getMaxVersionFromDb(dto.idUser),
            objects = repository
                .findAllByIdUserAndVersionGreaterThan(dto.idUser, dto.lastSyncVersion)
                .map { FinishedSpendingDto.fromEntity(it) }
        )
    }
}
