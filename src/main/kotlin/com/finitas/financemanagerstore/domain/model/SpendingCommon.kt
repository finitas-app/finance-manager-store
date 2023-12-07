package com.finitas.financemanagerstore.domain.model

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