package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
class User(
    @Id val internalId: String,
    val idUser: String,
    val visibleName: String,
    val regularSpendings: List<RegularSpending>
)

class RegularSpending(
    val idRegularSpending: String,
    val cron: String,
    val spendingSummary: SpendingSummary,
)
