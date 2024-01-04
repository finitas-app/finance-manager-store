package com.finitas.financemanagerstore.api.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.MoneySerializer
import com.finitas.financemanagerstore.domain.model.SpendingRecord
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.*

data class ResponseMessage(val message: String)

data class ErrorResponse(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
)

data class UpdateResponse(val lastSyncVersion: Int)

data class IdUserWithEntities<T> (
    val idUser: UUID,
    val changedValues: List<T>
)

data class FetchUpdatesResponse<T>(
    val updates: List<T>,
    val idUser: UUID,
    val actualVersion: Int,
)

data class SpendingRecordDto(
    val idSpendingRecordData: UUID,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    @field:JsonSerialize(using = MoneySerializer::class)
    val price: BigDecimal,
    val idCategory: UUID,
) {
    companion object {
        fun fromEntity(entity: SpendingRecord) = SpendingRecordDto(
            idSpendingRecordData = entity.idSpendingRecordData,
            name = entity.name,
            price = entity.price,
            idCategory = entity.idCategory,
        )
    }

    fun toEntity() = SpendingRecord(
        idSpendingRecordData = idSpendingRecordData,
        name = name,
        price = price,
        idCategory = idCategory,
    )
}
