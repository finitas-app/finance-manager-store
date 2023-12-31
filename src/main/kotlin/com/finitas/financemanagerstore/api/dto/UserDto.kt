package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.Category
import com.finitas.financemanagerstore.domain.model.RegularSpending
import com.finitas.financemanagerstore.domain.model.User
import jakarta.validation.constraints.Min
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

data class IdUserWithVersion(
    val userId: UUID,
    val version: Int
)

data class IdUserWithVisibleName(
    val idUser: UUID,
    val visibleName: String?,
)

data class UserDto(
    val idUser: UUID,
    val visibleName: String?,
    val version: Int,
    val regularSpendings: List<RegularSpendingDto>,
    val categories: List<CategoryDto>
) {

    companion object {
        fun fromEntity(entity: User, withRegularSpendings: Boolean = true) = UserDto(
            idUser = entity.idUser,
            visibleName = entity.visibleName,
            regularSpendings = if (withRegularSpendings)
                entity.regularSpendings.map { RegularSpendingDto.fromEntity(it) }
            else listOf(),
            categories = entity.categories.map { CategoryDto.fromEntity(it) },
            version = entity.version
        )
    }

    fun toEntity(version: Int) = User(
        internalId = idUser,
        idUser = idUser,
        visibleName = visibleName,
        regularSpendings = regularSpendings.map { it.toEntity() },

        categories = categories.map { it.toEntity(/*TODO: use spendingCategoryVersion in whole project*/-1) },
        version = version,
        // TODO: use spendingCategoryVersion in whole project
        spendingCategoryVersion = -1
    )
}

data class CategoryDto(
    val idCategory: UUID,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    val idParent: UUID?,
    val version: Int?,
    val isDeleted: Boolean,
) {

    fun toEntity(version: Int) = Category(
        idCategory = idCategory,
        name = name,
        idParent = idParent,
        version = version,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromEntity(it: Category) = CategoryDto(
            idCategory = it.idCategory,
            name = it.name,
            idParent = it.idParent,
            version = it.version,
            isDeleted = it.isDeleted,
        )
    }
}

data class RegularSpendingDto(
    val actualizationPeriod: Int,
    val periodUnit: Int,
    val lastActualizationDate: LocalDateTime,
    val idSpendingSummary: UUID,
    @field:Min(1, message = "createdAt should be a positive integer")
    val createdAt: Int,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:Size(min = 1, message = "spendingRecords should not be empty")
    val spendingRecords: List<SpendingRecordDto>,
) {
    companion object {
        fun fromEntity(entity: RegularSpending) = RegularSpendingDto(
            actualizationPeriod = entity.actualizationPeriod,
            periodUnit = entity.periodUnit,
            lastActualizationDate = entity.lastActualizationDate,
            createdAt = entity.createdAt,
            name = entity.name,
            spendingRecords = entity.spendingRecords.map { SpendingRecordDto.fromEntity(it) },
            idSpendingSummary = entity.idSpendingSummary
        )
    }

    fun toEntity() = RegularSpending(
        actualizationPeriod = actualizationPeriod,
        periodUnit = periodUnit,
        lastActualizationDate = lastActualizationDate,
        createdAt = createdAt,
        name = name,
        spendingRecords = spendingRecords.map { it.toEntity() },
        idSpendingSummary = idSpendingSummary
    )
}