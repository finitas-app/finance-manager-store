package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import com.finitas.financemanagerstore.domain.model.Receipt
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import com.finitas.financemanagerstore.domain.model.SpendingSummary
import org.springframework.core.io.ByteArrayResource
import javax.sql.rowset.serial.SerialBlob

data class FinishedSpendingDto(
    val version: Int,
    val idUser: String,
    val spendingSummary: SpendingSummaryDto,
    val receipt: ReceiptDto?,
    val purchaseData: Int,
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
    val idSpendingSummary: String,
    val createdAt: Int,
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