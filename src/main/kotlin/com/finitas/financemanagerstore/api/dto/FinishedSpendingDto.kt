package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import com.finitas.financemanagerstore.domain.model.SpendingSummary
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeleteFinishedSpendingRequest(
    @field:NotBlank(message = "idSpendingSummary should not be blank")
    val idSpendingSummary: String,
    @field:NotBlank(message = "idUser should not be blank")
    val idUser: String,
)

class FinishedSpendingDto(
    val spendingSummary: SpendingSummaryDto,
    val idReceipt: String?,
    @field:Min(0, message = "purchaseDate should be a non negative integer")
    val purchaseDate: Int,
    @field:Min(0, message = "version should be a non negative integer")
    val version: Int,
    @field:NotBlank(message = "idUser should not be blank")
    val idUser: String,
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

    fun toEntity(version: Int, internalId: String) = FinishedSpending(
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
    @field:NotBlank(message = "idSpendingSummary should not be blank")
    val idSpendingSummary: String,
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
    @field:NotBlank(message = "idSpendingRecord should not be blank")
    val idSpendingRecord: String,
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