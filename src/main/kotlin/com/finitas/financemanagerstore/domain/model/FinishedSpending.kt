package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Blob

class SpendingRecord(
    val idSpendingRecord: String,
    val spendingRecordData: SpendingRecordData,
)

class Receipt(
    val idReceipt: String,
    val photo: Blob
)

class SpendingSummary(
    val idSpendingSummary: String,
    val createdAt: Int,
    val name: String,
    val spendingRecords: List<SpendingRecord>,
)

@Document("finishedSpendings")
class FinishedSpending(
    @Id val internalId: String,
    val idSpendingSummary: String,
    val version: Int,
    val idUser: String,
    var isDeleted: Int,
    val spendingSummary: SpendingSummary,
    val receipt: Receipt?,
    val purchaseData: Int,
)