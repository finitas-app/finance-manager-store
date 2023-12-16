package com.finitas.financemanagerstore.domain.repositories

import com.finitas.financemanagerstore.domain.model.ShoppingList
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface ShoppingListRepository : MongoRepository<ShoppingList, UUID> {

    fun findAllByIdUserAndVersionGreaterThan(idUser: UUID, version: Int): List<ShoppingList>

    fun findByIdUser(idUser: UUID, sort: Sort, limit: Limit): List<ShoppingList>

    fun findByIdUserAndIdShoppingList(idUser: UUID, idShoppingList: UUID): ShoppingList?

    fun existsByIdUserAndIdShoppingList(idUser: UUID, idShoppingList: UUID): Boolean

    fun findAllByIdUser(idUser: UUID): List<ShoppingList>
}