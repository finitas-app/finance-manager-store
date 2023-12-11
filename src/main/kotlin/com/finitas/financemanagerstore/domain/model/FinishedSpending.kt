package com.finitas.financemanagerstore.domain.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
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
@CompoundIndexes(
    CompoundIndex(
        name = "idUserWithIdSpendingSummary",
        def = "{'spendingSummary.idSpendingSummary' : 1, 'idUser' : 1}",
        unique = true
    )
)
class FinishedSpending(
    val spendingSummary: SpendingSummary,
    val receipt: Receipt?,
    val purchaseDate: Int,
    internalId: String,
    version: Int,
    idUser: String,
    isDeleted: Boolean,
) : AbstractSpending(
    internalId = internalId,
    version = version,
    idUser = idUser,
    isDeleted = isDeleted,
)