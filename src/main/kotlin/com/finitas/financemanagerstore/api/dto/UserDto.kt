package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.RegularSpending
import com.finitas.financemanagerstore.domain.model.User

data class GetVisibleNamesRequest(
    val userIds: List<String>
)

data class IdUserWithVisibleName(
    val idUser: String,
    val visibleName: String,
)

data class UserDto(
    val idUser: String,
    val visibleName: String,
    val regularSpendings: List<RegularSpendingDto>
) {
    companion object {
        fun fromEntity(entity: User) = UserDto(
            idUser = entity.idUser,
            visibleName = entity.visibleName,
            regularSpendings = entity.regularSpendings.map { RegularSpendingDto.fromEntity(it) }
        )
    }

    fun toEntity() = User(
        idUser = idUser,
        visibleName = visibleName,
        regularSpendings = regularSpendings.map { it.toEntity() }
    )
}

data class RegularSpendingDto(
    val idRegularSpending: String,
    val cron: String,
    val spendingSummary: SpendingSummaryDto,
) {
    companion object {
        fun fromEntity(entity: RegularSpending) = RegularSpendingDto(
            idRegularSpending = entity.idRegularSpending,
            cron = entity.cron,
            spendingSummary = SpendingSummaryDto.fromEntity(entity.spendingSummary)
        )
    }

    fun toEntity() = RegularSpending(
        idRegularSpending = idRegularSpending,
        cron = cron,
        spendingSummary = spendingSummary.toEntity()
    )
}