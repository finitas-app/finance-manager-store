package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class DeleteFinishedSpendingRequest(
    val idSpendingSummary: UUID,
    val idUser: UUID,
)

data class FinishedSpendingDto(
    val idReceipt: UUID?,
    @field:Min(0, message = "purchaseDate should be a non negative long")
    val purchaseDate: Long,
    @field:Min(0, message = "version should be a non negative integer")
    val version: Int,
    val idUser: UUID,
    val isDeleted: Boolean,
    val idSpendingSummary: UUID,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:Size(min = 1, message = "spendingRecords should not be empty")
    val spendingRecords: List<SpendingRecordDto>,
) {
    companion object {
        fun fromEntity(entity: FinishedSpending) = FinishedSpendingDto(
            idUser = entity.idUser,
            purchaseDate = entity.purchaseDate,
            version = entity.version,
            isDeleted = entity.isDeleted,
            idReceipt = entity.idReceipt,
            name = entity.name,
            idSpendingSummary = entity.idSpendingSummary,
            spendingRecords = entity.spendingRecords.map { SpendingRecordDto.fromEntity(it) },
        )
    }

    fun toEntity(version: Int, internalId: UUID) = FinishedSpending(
        internalId = internalId,
        version = version,
        idUser = idUser,
        purchaseDate = purchaseDate,
        isDeleted = isDeleted,
        idReceipt = idReceipt,
        name = name,
        spendingRecords = spendingRecords.map { it.toEntity() },
        idSpendingSummary = idSpendingSummary,
    )
}
