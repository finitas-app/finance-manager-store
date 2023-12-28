package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("users")
class User(
    @Id val internalId: UUID,
    val idUser: UUID,
    var visibleName: String,
    var regularSpendings: List<RegularSpending>
)

class RegularSpending(
    val actualizationPeriod: Int,
    val periodUnit: Int,
    val lastActualizationDate: LocalDateTime,
    val spendingSummary: SpendingSummary,
)
