package com.finitas.financemanagerstore.domain.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

class ShoppingItem(
    val isDone: Int,
    val idShoppingItem: UUID,
    val spendingRecordData: SpendingRecordData,
)

@Document("shoppingLists")
@CompoundIndexes(
    CompoundIndex(name = "idUserWithIdShoppingList", def = "{'idShoppingList' : 1, 'idUser' : 1}", unique = true)
)
class ShoppingList(
    val idShoppingList: UUID,
    val shoppingItems: List<ShoppingItem>,
    internalId: UUID,
    version: Int,
    idUser: UUID,
    isDeleted: Boolean,
) : AbstractSpending(
    internalId = internalId,
    version = version,
    idUser = idUser,
    isDeleted = isDeleted,
)