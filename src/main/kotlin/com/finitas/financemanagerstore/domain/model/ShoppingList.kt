package com.finitas.financemanagerstore.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

class ShoppingItem(
    val isDone: Int,
    val idShoppingItem: String,
    val spendingRecordData: SpendingRecordData,
)

@Document("shoppingLists")
class ShoppingList(
    @Id val internalId: String,
    val idShoppingList: String,
    val version: Int,
    val shoppingItems: List<ShoppingItem>,
    val idUser: String,
    val isDeleted: Int,
)