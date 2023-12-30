package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.ShoppingItem
import com.finitas.financemanagerstore.domain.model.ShoppingList
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
    val isFinished: Boolean,
) {
    companion object {
        fun fromEntity(entity: ShoppingList) = ShoppingListDto(
            idShoppingList = entity.idShoppingList,
            idUser = entity.idUser,
            isDeleted = entity.isDeleted,
            version = entity.version,
            isFinished = entity.isFinished,
            shoppingItems = entity.shoppingItems.map { ShoppingItemDto.fromEntity(it) }
        )
    }

    fun toEntity(newVersion: Int, internalId: UUID) = ShoppingList(
        internalId = internalId,
        idShoppingList = idShoppingList,
        version = newVersion,
        idUser = idUser,
        isDeleted = isDeleted,
        isFinished = isFinished,
        shoppingItems = shoppingItems.map { it.toEntity() }
    )
}

data class ShoppingItemDto(
    val idShoppingItem: UUID,
    val amount: Int,
    val idSpendingRecordData: UUID,
    @field:NotBlank(message = "name should not be blank")
    val name: String,
    val idCategory: UUID,
) {
    companion object {
        fun fromEntity(entity: ShoppingItem) = ShoppingItemDto(
            amount = entity.amount,
            idShoppingItem = entity.idShoppingItem,
            idSpendingRecordData = entity.idSpendingRecordData,
            name = entity.name,
            idCategory = entity.idCategory
        )
    }

    fun toEntity() = ShoppingItem(
        amount = amount,
        idShoppingItem = idShoppingItem,
        idSpendingRecordData = idSpendingRecordData,
        name = name,
        idCategory = idCategory
    )
}
