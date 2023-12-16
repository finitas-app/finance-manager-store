package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.util.*

class Category(
    val idCategory: UUID,
    val name: String,
    val idParent: UUID?,
)

class SpendingRecordData(
    val idSpendingRecordData: UUID,
    val name: String,
    val price: BigDecimal,
    val category: Category,
)

open class AbstractSpending(
    @Id val internalId: UUID,
    var version: Int,
    val idUser: UUID,
    var isDeleted: Boolean,
)