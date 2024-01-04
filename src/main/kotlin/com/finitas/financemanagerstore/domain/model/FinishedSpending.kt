package com.finitas.financemanagerstore.domain.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class SpendingRecord(
    val idSpendingRecordData: UUID,
    val name: String,
    val price: BigDecimal,
    val idCategory: UUID,
)

@Document("finishedSpendings")
@CompoundIndexes(
    CompoundIndex(
        name = "idUserWithIdSpendingSummary",
        def = "{'idSpendingSummary' : 1, 'idUser' : 1}",
        unique = true
    )
)
class FinishedSpending(
    internalId: UUID,
    version: Int,
    idUser: UUID,
    isDeleted: Boolean,
    val idReceipt: UUID?,
    val purchaseDate: Long,
    val idSpendingSummary: UUID,
    val name: String,
    val spendingRecords: List<SpendingRecord>,
) : AbstractSpending(
    internalId = internalId,
    version = version,
    idUser = idUser,
    isDeleted = isDeleted,
)