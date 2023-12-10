package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.Category
import com.finitas.financemanagerstore.domain.model.SpendingRecordData
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class ResponseMessage(val message: String)

data class SynchronizationRequest<T>(
    @Min(0)
    val lastSyncVersion: Int,
    val isAuthorDataToUpdate: Boolean,
    val objects: List<T>
)

data class SynchronizationResponse<T>(
    val actualizedSyncVersion: Int,
    val objects: List<T>
)

data class SpendingRecordDataDto(
    @NotBlank
    val idSpendingRecordData: String,
    @NotBlank
    val name: String,
    @Min(0)
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
    @NotBlank
    val idCategory: String,
    @NotBlank
    val name: String,
    @NotBlank
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
