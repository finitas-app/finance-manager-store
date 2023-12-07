package com.finitas.financemanagerstore.domain.repositories

import com.finitas.financemanagerstore.domain.model.ShoppingList
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository


interface ShoppingListRepository : MongoRepository<ShoppingList, String> {

    fun findAllByIdUserAndVersionGreaterThan(idUser: String, version: Int): List<ShoppingList>

    fun findByIdUser(idUser: String, sort: Sort, limit: Limit): List<ShoppingList>

    fun findByIdUserAndIdShoppingList(idUser: String, idShoppingList: String): ShoppingList?
}