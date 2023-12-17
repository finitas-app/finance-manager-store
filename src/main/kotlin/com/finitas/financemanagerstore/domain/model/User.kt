package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("users")
class User(
    @Id val internalId: UUID,
    val idUser: UUID,
    var visibleName: String,
    var regularSpendings: List<RegularSpending>
)

class RegularSpending(
    val idRegularSpending: UUID,
    val cron: String,
    val spendingSummary: SpendingSummary,
)
