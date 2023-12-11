package com.finitas.financemanagerstore.domain.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

class ShoppingItem(
    val isDone: Int,
    val idShoppingItem: String,
    val spendingRecordData: SpendingRecordData,
)

@Document("shoppingLists")
@CompoundIndexes(
    CompoundIndex(name = "idUserWithIdShoppingList", def = "{'idShoppingList' : 1, 'idUser' : 1}", unique = true)
)
class ShoppingList(
    val idShoppingList: String,
    val shoppingItems: List<ShoppingItem>,
    internalId: String,
    version: Int,
    idUser: String,
    isDeleted: Boolean,
) : AbstractSpending(
    internalId = internalId,
    version = version,
    idUser = idUser,
    isDeleted = isDeleted,
)