package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.GetVisibleNamesRequest
import com.finitas.financemanagerstore.api.dto.IdUserWithVisibleName
import com.finitas.financemanagerstore.api.dto.RegularSpendingDto
import com.finitas.financemanagerstore.api.dto.UserDto
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.User
import com.finitas.financemanagerstore.domain.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserService(private val repository: UserRepository) {

    @Transactional
    fun upsertUser(dto: UserDto) {
        repository.save(dto.toEntity())
    }

    @Transactional
    fun addNewRegularSpendings(idUser: String, newSpendings: List<RegularSpendingDto>) {
        val userFromRepo = repository.findByIdUser(idUser).firstOrNull() ?: throw NotFoundException("User not found")

        val spendingGroupedById = userFromRepo.regularSpendings.associateBy { it.idRegularSpending }.toMutableMap()
        newSpendings.forEach {
            spendingGroupedById[it.idRegularSpending] = it.toEntity()
        }

        repository.save(
            User(
                idUser = idUser,
                visibleName = userFromRepo.visibleName,
                internalId = userFromRepo.internalId,
                regularSpendings = spendingGroupedById.values.toList()
            )
        )
    }

    fun getVisibleNames(request: GetVisibleNamesRequest) =
        repository.findByIdUserIn(request.userIds).map { IdUserWithVisibleName(it.idUser, it.visibleName) }
}