package com.finitas.financemanagerstore.api.dto

import com.finitas.financemanagerstore.domain.model.ShoppingItem
import com.finitas.financemanagerstore.domain.model.ShoppingList

data class ShoppingListDto(
    val idShoppingList: String,
    val shoppingItems: List<ShoppingItemDto>,
    val idUser: String,
    val isDeleted: Int,
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
    val idShoppingItem: String,
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
