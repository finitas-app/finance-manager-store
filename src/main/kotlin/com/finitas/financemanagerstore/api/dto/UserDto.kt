package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.RegularSpending
import com.finitas.financemanagerstore.domain.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GetVisibleNamesRequest(
    @field:Size(min = 1, message = "userIds should not be empty")
    val userIds: List<String>
)

data class IdUserWithVisibleName(
    @field:NotBlank(message = "idUser should not be blank")
    val idUser: String,
    @field:NotBlank(message = "visibleName should not be blank")
    val visibleName: String,
)

data class UserDto(
    @field:NotBlank(message = "idUser should not be blank")
    val idUser: String,
    @field:NotBlank(message = "visibleName should not be blank")
    val visibleName: String,
    val regularSpendings: List<RegularSpendingDto>
) {

    fun toEntity() = User(
        internalId = idUser,
        idUser = idUser,
        visibleName = visibleName,
        regularSpendings = regularSpendings.map { it.toEntity() }
    )
}

data class RegularSpendingDto(
    @field:NotBlank(message = "idRegularSpending should not be blank")
    val idRegularSpending: String,
    @field:NotBlank(message = "cron should not be blank")
    val cron: String,
    val spendingSummary: SpendingSummaryDto,
) {

    fun toEntity() = RegularSpending(
        idRegularSpending = idRegularSpending,
        cron = cron,
        spendingSummary = spendingSummary.toEntity()
    )
}