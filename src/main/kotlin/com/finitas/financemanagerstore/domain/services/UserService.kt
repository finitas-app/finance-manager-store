package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.GetVisibleNamesRequest
import com.finitas.financemanagerstore.api.dto.IdUserWithVisibleName
import com.finitas.financemanagerstore.api.dto.RegularSpendingDto
import com.finitas.financemanagerstore.api.dto.UserDto
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.User
import com.finitas.financemanagerstore.domain.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Component
class UserService(private val repository: UserRepository) {

    @Transactional
    fun upsertUser(dto: UserDto) {
        repository.save(dto.toEntity())
    }

    @Transactional
    fun addNewRegularSpendings(idUser: UUID, newSpendings: List<RegularSpendingDto>) {
        val userFromRepo = repository
            .findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }

        val spendingGroupedById = userFromRepo.regularSpendings.associateBy { it.idRegularSpending }.toMutableMap()
        newSpendings.forEach {
            spendingGroupedById[it.idRegularSpending] = it.toEntity()
        }

        repository.save(
            User(
                idUser = idUser,
                visibleName = userFromRepo.visibleName,
                internalId = userFromRepo.internalId,
                regularSpendings = spendingGroupedById.values.toMutableList()
            )
        )
    }

    fun getVisibleNames(request: GetVisibleNamesRequest) =
        repository.findAllById(request.userIds.map { it.userId })
            .map { IdUserWithVisibleName(it.idUser, it.visibleName) }

    fun updateVisibleName(request: IdUserWithVisibleName) {
        repository.findById(request.idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .apply { visibleName = request.visibleName }
            .also { repository.save(it) }
    }

    fun getUserRegularSpendings(idUser: UUID) =
        repository
            .findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .regularSpendings
            .map { RegularSpendingDto.fromEntity(it) }

    fun deleteUserRegularSpending(idUser: UUID, idRegularSpending: UUID) {
        repository.findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .apply { regularSpendings = regularSpendings.filter { it.idRegularSpending != idRegularSpending } }
            .also { repository.save(it) }
    }
}