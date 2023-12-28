package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.RegularSpending
import com.finitas.financemanagerstore.domain.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class UserIdValue(
    val userId: UUID,
)

data class GetVisibleNamesRequest(
    @field:Size(min = 1, message = "userIds should not be empty")
    val userIds: List<UserIdValue>
)

data class IdUserWithVisibleName(
    val idUser: UUID,
    @field:NotBlank(message = "visibleName should not be blank")
    val visibleName: String,
)

data class UserDto(
    val idUser: UUID,
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
    val actualizationPeriod: Int,
    val periodUnit: Int,
    val lastActualizationDate: LocalDateTime,
    val spendingSummary: SpendingSummaryDto,
) {
    companion object {
        fun fromEntity(entity: RegularSpending) = RegularSpendingDto(
            actualizationPeriod = entity.actualizationPeriod,
            periodUnit = entity.periodUnit,
            lastActualizationDate = entity.lastActualizationDate,
            spendingSummary = SpendingSummaryDto.fromEntity(entity.spendingSummary)
        )
    }

    fun toEntity() = RegularSpending(
        actualizationPeriod = actualizationPeriod,
        periodUnit = periodUnit,
        lastActualizationDate = lastActualizationDate,
        spendingSummary = spendingSummary.toEntity()
    )
}