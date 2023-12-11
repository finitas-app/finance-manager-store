package com.finitas.financemanagerstore.domain.repositories

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository


interface FinishedSpendingRepository : MongoRepository<FinishedSpending, String> {

    fun findAllByIdUserAndVersionGreaterThan(idUser: String, version: Int): List<FinishedSpending>

    fun findByIdUser(idUser: String, sort: Sort, limit: Limit): List<FinishedSpending>

    fun findAllByIdUser(idUser: String): List<FinishedSpending>

    fun findByIdUserAndSpendingSummaryIdSpendingSummary(idUser: String, idSpendingSummary: String): FinishedSpending?

    fun existsByIdUserAndSpendingSummaryIdSpendingSummary(idUser: String, idSpendingSummary: String): Boolean
}