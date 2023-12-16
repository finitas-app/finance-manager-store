package com.finitas.financemanagerstore.domain.repositories

import com.finitas.financemanagerstore.domain.model.FinishedSpending
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface FinishedSpendingRepository : MongoRepository<FinishedSpending, UUID> {

    fun findAllByIdUserAndVersionGreaterThan(idUser: UUID, version: Int): List<FinishedSpending>

    fun findByIdUser(idUser: UUID, sort: Sort, limit: Limit): List<FinishedSpending>

    fun findAllByIdUser(idUser: UUID): List<FinishedSpending>

    fun findByIdUserAndSpendingSummaryIdSpendingSummary(idUser: UUID, idSpendingSummary: UUID): FinishedSpending?

    fun existsByIdUserAndSpendingSummaryIdSpendingSummary(idUser: UUID, idSpendingSummary: UUID): Boolean
}