package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.validate
import com.finitas.financemanagerstore.domain.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/store/users")
class UserController(private val service: UserService) {

    @PutMapping
    fun upsertUser(@Valid @RequestBody request: UserDto, errors: Errors): ResponseMessage {
        errors.validate()
        service.upsertUser(request)
        return ResponseMessage("success")
    }

    @PostMapping("{idUser}/regular-spendings")
    fun addRegularSpendings(
        @PathVariable idUser: UUID,
        @Valid @RequestBody regularSpendings: List<RegularSpendingDto>,
        errors: Errors
    ): ResponseMessage {
        errors.validate()
        service.addNewRegularSpendings(idUser, regularSpendings)
        return ResponseMessage("success")
    }

    @GetMapping("nicknames")
    fun getNicknames(@Valid @RequestBody request: GetVisibleNamesRequest, errors: Errors): List<IdUserWithVisibleName> {
        errors.validate()
        return service.getVisibleNames(request)
    }

    @GetMapping("{idUser}")
    fun getUser(@PathVariable idUser: UUID): UserDto {
        return service.getUser(idUser)
    }

    @GetMapping
    fun getUser(@RequestBody userIds: List<IdUserWithVersion>): List<UserDto> {
        return service.getUsers(userIds)
    }

    @PatchMapping("nicknames")
    fun updateNickname(@Valid @RequestBody request: IdUserWithVisibleName, errors: Errors): ResponseEntity<Unit> {
        errors.validate()
        service.updateVisibleName(request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("{idUser}/regular-spendings")
    fun getUserRegularSpendings(@PathVariable idUser: UUID): List<RegularSpendingDto> {
        return service.getUserRegularSpendings(idUser)
    }

    @DeleteMapping("{idUser}/regular-spendings/{idSpendingSummary}")
    fun deleteUserRegularSpendings(
        @PathVariable idUser: UUID,
        @PathVariable idSpendingSummary: UUID
    ): ResponseEntity<Unit> {
        service.deleteUserRegularSpending(idUser = idUser, idSpendingSummary = idSpendingSummary)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("{idUser}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun overrideCategories(
        @PathVariable idUser: UUID,
        // TODO: Add validation
        @RequestBody changeSpendingCategoryDto: ChangeSpendingCategoryDto,
    ) {
        service.overrideCategories(idUser, changeSpendingCategoryDto)
    }

    @PostMapping("categories/sync")
    @ResponseStatus(HttpStatus.OK)
    fun getCategoriesFromVersion(
        // TODO: Add validation
        @RequestBody syncCategoriesRequest: SyncCategoriesRequest,
    ): GetCategoriesFromVersionResponse {
        return service.getCategoriesFromVersion(syncCategoriesRequest)
    }
}


data class ChangeSpendingCategoryDto(
    val spendingCategories: List<SpendingCategoryDto>,
)

data class SpendingCategoryDto(
    val name: String,
    val idParent: UUID?,
    val idUser: UUID?,
    val idCategory: UUID,
    val isDeleted: Boolean,
)

data class SyncCategoriesRequest(
    val userVersions: List<CategoryVersionDto>,
)

data class CategoryVersionDto(
    val idUser: UUID,
    val version: Int,
)


data class GetCategoriesFromVersionResponse(
    val userCategories: List<UserWithCategoriesDto>,
)


data class UserWithCategoriesDto(
    val idUser: UUID,
    val categoryVersion: Int,
    val categories: List<CategoryDto>,
)
