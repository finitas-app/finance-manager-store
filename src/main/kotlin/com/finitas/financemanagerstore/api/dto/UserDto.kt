package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.RegularSpending
import com.finitas.financemanagerstore.domain.model.User
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GetVisibleNamesRequest(
    @Size(min = 1)
    val userIds: List<String>
)

data class IdUserWithVisibleName(
    @NotBlank
    val idUser: String,
    @NotBlank
    val visibleName: String,
)

data class UserDto(
    @NotBlank
    val idUser: String,
    @NotBlank
    val visibleName: String,
    val regularSpendings: List<RegularSpendingDto>
) {

    fun toEntity() = User(
        idUser = idUser,
        visibleName = visibleName,
        regularSpendings = regularSpendings.map { it.toEntity() }
    )
}

data class RegularSpendingDto(
    @NotBlank
    val idRegularSpending: String,
    @NotBlank
    val cron: String,
    val spendingSummary: SpendingSummaryDto,
) {

    fun toEntity() = RegularSpending(
        idRegularSpending = idRegularSpending,
        cron = cron,
        spendingSummary = spendingSummary.toEntity()
    )
}