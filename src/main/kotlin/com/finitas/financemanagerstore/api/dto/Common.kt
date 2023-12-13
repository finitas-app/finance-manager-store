package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.domain.model.Category
import com.finitas.financemanagerstore.domain.model.SpendingRecordData
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ResponseMessage(val message: String)

data class ErrorResponse(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
)

data class UpdateResponse(val lastSyncVersion: Int)

data class SynchronizationRequest<T>(
    @field:Min(0, message = "lastSyncVersion should be a non negative integer")
    val lastSyncVersion: Int,
    val isAuthorDataToUpdate: Boolean,
    @field:Size(min = 1, message = "Update data list should not be empty")
    val objects: List<T>
)

data class SynchronizationResponse<T>(
    val actualizedSyncVersion: Int,
    val objects: List<T>
)

data class SpendingRecordDataDto(
    @field:NotBlank(message = "idSpendingRecordData should not be blank")
    val idSpendingRecordData: String,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:Min(0, message = "price should be a non negative integer")
    val price: BigDecimal,
    val category: CategoryDto,
) {
    companion object {
        fun fromEntity(entity: SpendingRecordData) = SpendingRecordDataDto(
            idSpendingRecordData = entity.idSpendingRecordData,
            name = entity.name,
            price = entity.price,
            category = CategoryDto.fromEntity(entity.category)
        )
    }

    fun toEntity() = SpendingRecordData(
        idSpendingRecordData = idSpendingRecordData,
        name = name,
        price = price,
        category = category.toEntity()
    )
}

data class CategoryDto(
    @field:NotBlank(message = "idCategory should not be blank")
    val idCategory: String,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:NotBlank(message = "idParent should not be blank")
    val idParent: String?,
) {
    companion object {
        fun fromEntity(entity: Category) = CategoryDto(
            idCategory = entity.idCategory,
            name = entity.name,
            idParent = entity.idParent,
        )
    }

    fun toEntity() = Category(
        idCategory = idCategory,
        name = name,
        idParent = idParent,
    )
}
