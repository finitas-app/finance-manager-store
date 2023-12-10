package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.model.Receipt
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import com.finitas.financemanagerstore.domain.model.SpendingSummary
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.core.io.ByteArrayResource
import javax.sql.rowset.serial.SerialBlob

data class DeleteFinishedSpendingRequest (
    @NotBlank
    val idSpendingSummary: String,
    @NotBlank
    val idUser: String,
)

data class FinishedSpendingDto(
    @Min(0)
    val version: Int,
    @NotBlank
    val idUser: String,
    val spendingSummary: SpendingSummaryDto,
    val receipt: ReceiptDto?,
    @Min(0)
    val purchaseData: Int,
    @Min(0) @Max(1)
    val isDeleted: Int,
) {
    companion object {
        fun fromEntity(entity: FinishedSpending) = FinishedSpendingDto(
            version = entity.version,
            idUser = entity.idUser,
            purchaseData = entity.purchaseData,
            receipt = entity.receipt?.let { ReceiptDto.fromEntity(it) },
            spendingSummary = SpendingSummaryDto.fromEntity(entity.spendingSummary),
            isDeleted = entity.isDeleted
        )
    }

    fun toEntity(version: Int, internalId: String) = FinishedSpending(
        version = version,
        idUser = idUser,
        purchaseData = purchaseData,
        isDeleted = isDeleted,
        receipt = receipt?.toEntity(),
        spendingSummary = spendingSummary.toEntity(),
        idSpendingSummary = spendingSummary.idSpendingSummary,
        internalId = internalId,
    )
}

data class SpendingSummaryDto(
    @NotBlank
    val idSpendingSummary: String,
    @Min(1)
    val createdAt: Int,
    @NotBlank
    val name: String,
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

data class ReceiptDto(
    @NotBlank
    val idReceipt: String,
    val photo: ByteArrayResource,
) {
    companion object {
        fun fromEntity(entity: Receipt) = ReceiptDto(
            idReceipt = entity.idReceipt,
            photo = entity.photo
                .getBytes(1, entity.photo.length().toInt())
                .let { ByteArrayResource(it) }
        )
    }

    fun toEntity() = Receipt(
        idReceipt = idReceipt,
        photo = SerialBlob(photo.byteArray),
    )
}

data class SpendingRecordDto(
    @NotBlank
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