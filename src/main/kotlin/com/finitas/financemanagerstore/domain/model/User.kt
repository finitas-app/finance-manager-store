package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("users")
data class User(
    @Id val internalId: UUID,
    val idUser: UUID,
    val version: Int,
    var spendingCategoryVersion: Int,
    var visibleName: String?,
    var regularSpendings: List<RegularSpending>,
    var categories: List<Category>
)

data class RegularSpending(
    val actualizationPeriod: Int,
    val periodUnit: Int,
    val lastActualizationDate: LocalDateTime,
    val idSpendingSummary: UUID,
    val createdAt: Int,
    val name: String,
    val spendingRecords: List<SpendingRecord>,
)

class Category(
    val idCategory: UUID,
    val name: String,
    val idParent: UUID?,
    val version: Int,
    val isDeleted: Boolean,
)

