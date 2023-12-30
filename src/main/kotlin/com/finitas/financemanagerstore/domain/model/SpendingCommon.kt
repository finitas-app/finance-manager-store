package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import java.util.*

open class AbstractSpending(
    @Id val internalId: UUID,
    var version: Int,
    val idUser: UUID,
    var isDeleted: Boolean,
)