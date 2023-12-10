package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.ShoppingItem
import com.finitas.financemanagerstore.domain.model.ShoppingList
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeleteShoppingListRequest (
    @NotBlank
    val idShoppingItem: String,
    @NotBlank
    val idUser: String,
)

data class ShoppingListDto(
    @NotBlank
    val idShoppingList: String,
    @Size(min = 1)
    val shoppingItems: List<ShoppingItemDto>,
    @NotBlank
    val idUser: String,
    @Min(0) @Max(1)
    val isDeleted: Int,
    @Min(0)
    val version: Int,
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

    fun toEntity(newVersion: Int, internalId: String) = ShoppingList(
        internalId = internalId,
        idShoppingList = idShoppingList,
        version = newVersion,
        idUser = idUser,
        isDeleted = isDeleted,
        shoppingItems = shoppingItems.map { it.toEntity() }
    )
}

data class ShoppingItemDto(
    @NotBlank
    val idShoppingItem: String,
    @Min(0) @Max(1)
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
