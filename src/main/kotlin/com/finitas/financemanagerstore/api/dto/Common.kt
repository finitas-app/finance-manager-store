package com.finitas.financemanagerstore.api.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.MoneySerializer
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.*

data class ResponseMessage(val message: String)

data class ErrorResponse(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
)

data class UpdateResponse(val lastSyncVersion: Int)

data class SynchronizationRequest<T>(
    @field:Min(0, message = "lastSyncVersion should be a non negative integer")
    val lastSyncVersion: Int,
    val idUser: UUID,
    val isAuthorDataToUpdate: Boolean,
    val objects: List<T>
)

data class SynchronizationResponse<T>(
    val actualizedSyncVersion: Int,
    val objects: List<T>
)

data class SpendingRecordDto(
    val idSpendingRecord: UUID,
    val idSpendingRecordData: UUID,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:JsonSerialize(using = MoneySerializer::class)
    val price: BigDecimal,
    val idCategory: UUID,
) {
    companion object {
        fun fromEntity(entity: SpendingRecord) = SpendingRecordDto(
            idSpendingRecord = entity.idSpendingRecord,
            idSpendingRecordData = entity.idSpendingRecordData,
            name = entity.name,
            price = entity.price,
            idCategory = entity.idCategory,
        )
    }

    fun toEntity() = SpendingRecord(
        idSpendingRecord = idSpendingRecord,
        idSpendingRecordData = idSpendingRecordData,
        name = name,
        price = price,
        idCategory = idCategory,
    )
}
