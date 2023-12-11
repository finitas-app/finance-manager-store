package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import java.math.BigDecimal

class Category(
    val idCategory: String,
    val name: String,
    val idParent: String?,
)

class SpendingRecordData(
    val idSpendingRecordData: String,
    val name: String,
    val price: BigDecimal,
    val category: Category,
)

open class AbstractSpending(
    @Id val internalId: String,
    var version: Int,
    val idUser: String,
    var isDeleted: Boolean,
)