package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.dto.*
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
        repository.save(dto.toEntity(dto.version + 1))
    }

    @Transactional
    fun addNewRegularSpendings(idUser: UUID, newSpendings: List<RegularSpendingDto>) {
        val userFromRepo = repository
            .findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }

        val spendingGroupedById =
            userFromRepo.regularSpendings.associateBy { it.idSpendingSummary }.toMutableMap()
        newSpendings.forEach {
            spendingGroupedById[it.idSpendingSummary] = it.toEntity()
        }

        repository.save(
            User(
                idUser = idUser,
                visibleName = userFromRepo.visibleName,
                internalId = userFromRepo.internalId,
                regularSpendings = spendingGroupedById.values.toMutableList(),
                categories = userFromRepo.categories,
                // todo: check if increment is needed
                version = userFromRepo.version
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
            .also {
                repository.save(it.copy(version = it.version + 1))
            }
    }

    fun getUserRegularSpendings(idUser: UUID) =
        repository
            .findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .regularSpendings
            .map { RegularSpendingDto.fromEntity(it) }

    fun deleteUserRegularSpending(idUser: UUID, idSpendingSummary: UUID) {
        repository.findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .apply {
                regularSpendings = regularSpendings.filter { it.idSpendingSummary != idSpendingSummary }
            }
            .also { repository.save(it) }
    }

    fun getUser(idUser: UUID) =
        repository.findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND) }
            .let { UserDto.fromEntity(it) }

    fun getUsers(userIds: List<IdUserWithVersion>): List<UserDto> {
        val groupedById = userIds.associateBy { it.userId }
        return repository.findAllById(groupedById.keys)
            .filter { it.version <= groupedById[it.idUser]!!.version }
            .map { UserDto.fromEntity(it, false) }
    }
}