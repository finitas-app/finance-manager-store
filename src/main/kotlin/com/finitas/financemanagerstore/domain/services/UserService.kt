package com.finitas.financemanagerstore.domain.services

import com.finitas.financemanagerstore.api.controllers.*
import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.ErrorCode
import com.finitas.financemanagerstore.config.NotFoundException
import com.finitas.financemanagerstore.domain.model.Category
import com.finitas.financemanagerstore.domain.model.User
import com.finitas.financemanagerstore.domain.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Component
@Transactional
class UserService(private val repository: UserRepository) {

    fun upsertUser(dto: UserDto) {
        repository.save(dto.toEntity(dto.version + 1))
    }

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
                version = userFromRepo.version,
                // todo: modify system to use it
                spendingCategoryVersion = -1,
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
        val groupedById = userIds.associateBy { it.idUser }
        return repository.findAllById(groupedById.keys)
            .filter { it.version <= groupedById[it.idUser]!!.version }
            .map { UserDto.fromEntity(it, false) }
    }

    fun overrideCategories(idUser: UUID, changeSpendingCategoryDto: ChangeSpendingCategoryDto) {
        val categoriesIds = changeSpendingCategoryDto.spendingCategories.map { it.idCategory }.toSet()
        repository.findById(idUser)
            .getOrElse { throw NotFoundException(ErrorCode.USER_NOT_FOUND, "User not found") }
            .let { user ->
                val newVersion = user.spendingCategoryVersion + 1
                val newCategories = changeSpendingCategoryDto.spendingCategories.toModel(newVersion)
                repository.save(
                    user.copy(
                        categories = user
                            .categories
                            .filter { it.idCategory !in categoriesIds } + newCategories,
                        spendingCategoryVersion = newVersion
                    )
                )
            }
    }

    fun getCategoriesFromVersion(syncCategoriesRequest: SyncCategoriesRequest): GetCategoriesFromVersionResponse {
        return syncCategoriesRequest.userVersions.map { categoryVersion ->
            val user = repository.findById(categoryVersion.idUser).getOrNull() ?: return@map null
            if (user.spendingCategoryVersion == categoryVersion.version) return@map null

            UserWithCategoriesDto(
                user.idUser,
                user.spendingCategoryVersion,
                user.categories.filter { it.version > categoryVersion.version }.map { CategoryDto.fromEntity(it) }
            )
        }.let { GetCategoriesFromVersionResponse(it.filterNotNull()) }
    }
}

private fun List<SpendingCategoryDto>.toModel(version: Int) = map {
    Category(
        idCategory = it.idCategory,
        name = it.name,
        idParent = it.idParent,
        version = version,
        isDeleted = it.isDeleted,
    )
}
