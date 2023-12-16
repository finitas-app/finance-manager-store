package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import com.finitas.financemanagerstore.domain.model.SpendingSummary
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class DeleteFinishedSpendingRequest(
    val idSpendingSummary: UUID,
    val idUser: UUID,
)

class FinishedSpendingDto(
    val spendingSummary: SpendingSummaryDto,
    val idReceipt: UUID?,
    @field:Min(0, message = "purchaseDate should be a non negative integer")
    val purchaseDate: Int,
    @field:Min(0, message = "version should be a non negative integer")
    val version: Int,
    val idUser: UUID,
    val isDeleted: Boolean,
) {
    companion object {
        fun fromEntity(entity: FinishedSpending) = FinishedSpendingDto(
            idUser = entity.idUser,
            purchaseDate = entity.purchaseDate,
            version = entity.version,
            isDeleted = entity.isDeleted,
            spendingSummary = SpendingSummaryDto.fromEntity(entity.spendingSummary),
            idReceipt = entity.idReceipt
        )
    }

    fun toEntity(version: Int, internalId: UUID) = FinishedSpending(
        version = version,
        idUser = idUser,
        purchaseDate = purchaseDate,
        isDeleted = isDeleted,
        idReceipt = idReceipt,
        spendingSummary = spendingSummary.toEntity(),
        internalId = internalId,
    )
}

data class SpendingSummaryDto(
    val idSpendingSummary: UUID,
    @field:Min(1, message = "createdAt should be a positive integer")
    val createdAt: Int,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:Size(min = 1, message = "spendingRecords should not be empty")
    val spendingRecords: List<SpendingRecordDto>,
) {
    companion object {
        fun fromEntity(entity: SpendingSummary) = SpendingSummaryDto(
            createdAt = entity.createdAt,
            name = entity.name,
            spendingRecords = entity.spendingRecords.map { SpendingRecordDto.fromEntity(it) },
            idSpendingSummary = entity.idSpendingSummary
        )
    }

    fun toEntity() = SpendingSummary(
        createdAt = createdAt,
        name = name,
        spendingRecords = spendingRecords.map { it.toEntity() },
        idSpendingSummary = idSpendingSummary
    )
}

data class SpendingRecordDto(
    val idSpendingRecord: UUID,
    val spendingRecordData: SpendingRecordDataDto,
) {
    companion object {
        fun fromEntity(entity: SpendingRecord) = SpendingRecordDto(
            idSpendingRecord = entity.idSpendingRecord,
            spendingRecordData = SpendingRecordDataDto.fromEntity(entity.spendingRecordData)
        )
    }

    fun toEntity() = SpendingRecord(
        idSpendingRecord = idSpendingRecord,
        spendingRecordData = spendingRecordData.toEntity(),
    )
}