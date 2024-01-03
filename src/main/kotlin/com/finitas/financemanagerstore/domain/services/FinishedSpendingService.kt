package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.*
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@Service
@Transactional
class FinishedSpendingService(
    private val repository: FinishedSpendingRepository,
    private val mongoTemplate: MongoTemplate
) {

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

    fun insert(dto: FinishedSpendingDto, newItemVersion: Int = getMaxVersionFromDb(dto.idUser) + 1): Int {
        try {
            repository.save(dto.toEntity(newItemVersion, UUID.randomUUID()))
        } catch (_: DuplicateKeyException) {
            throw ConflictException(ErrorCode.FINISHED_SPENDING_NOT_FOUND, "Finished spending already exists")
        }

        return newItemVersion
    }

    fun update(dto: FinishedSpendingDto, newItemVersion: Int = getMaxVersionFromDb(dto.idUser) + 1): Int {
        val entity = repository.findByIdUserAndIdSpendingSummary(
            dto.idUser,
            dto.idSpendingSummary
        )
            ?: throw NotFoundException(ErrorCode.FINISHED_SPENDING_EXISTS, "Finished spending not found")

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

    fun updateWithChangedItems(dto: IdUserWithEntities<FinishedSpendingDto>) {
        val currentMaxVersion = AtomicInteger(getMaxVersionFromDb(dto.idUser))
        dto.changedValues
            .forEach {
                val isExists = repository.existsByIdUserAndIdSpendingSummary(
                    idUser = it.idUser,
                    idSpendingSummary = it.idSpendingSummary
                )

                if (isExists) update(it, currentMaxVersion.incrementAndGet())
                else insert(it, currentMaxVersion.incrementAndGet())
            }
    }

    fun fetchUsersUpdates(request: List<IdUserWithVersion>): List<FetchUpdatesResponse<FinishedSpendingDto>> {
        return request.mapNotNull {
            val updates = repository
                .findAllByIdUserAndVersionGreaterThan(
                    idUser = it.idUser,
                    version = it.version
                )
                .map { entity -> FinishedSpendingDto.fromEntity(entity) }

            updates.maxOfOrNull { update -> update.version }?.let { max ->
                FetchUpdatesResponse(
                    updates = updates,
                    idUser = it.idUser,
                    actualVersion = max
                )
            }
        }
    }
}
