package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.ShoppingItem
import com.finitas.financemanagerstore.domain.model.ShoppingList
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class DeleteShoppingListRequest(
    val idShoppingList: UUID,
    val idUser: UUID,
)

class ShoppingListDto(
    val idShoppingList: UUID,
    @field:Size(min = 1, message = "idShoppingList should not be empty")
    val shoppingItems: List<ShoppingItemDto>,
    @field:Min(0, message = "version should be a non negative integer")
    val version: Int,
    val idUser: UUID,
    val isDeleted: Boolean,
) {
    companion object {
        fun fromEntity(entity: ShoppingList) = ShoppingListDto(
            idShoppingList = entity.idShoppingList,
            idUser = entity.idUser,
            isDeleted = entity.isDeleted,
            version = entity.version,
            shoppingItems = entity.shoppingItems.map { ShoppingItemDto.fromEntity(it) }
        )
    }

    fun toEntity(newVersion: Int, internalId: UUID) = ShoppingList(
        internalId = internalId,
        idShoppingList = idShoppingList,
        version = newVersion,
        idUser = idUser,
        isDeleted = isDeleted,
        shoppingItems = shoppingItems.map { it.toEntity() }
    )
}

data class ShoppingItemDto(
    val idShoppingItem: UUID,
    @field:Min(0, message = "isDone flag can be of value 0 or 1")
    @field:Max(1, message = "isDone flag can be of value 0 or 1")
    val isDone: Int,
    val spendingRecordData: SpendingRecordDataDto,
) {
    companion object {
        fun fromEntity(entity: ShoppingItem) = ShoppingItemDto(
            isDone = entity.isDone,
            idShoppingItem = entity.idShoppingItem,
            spendingRecordData = SpendingRecordDataDto.fromEntity(entity.spendingRecordData)
        )
    }

    fun toEntity() = ShoppingItem(
        isDone = isDone,
        idShoppingItem = idShoppingItem,
        spendingRecordData = spendingRecordData.toEntity()
    )
}
