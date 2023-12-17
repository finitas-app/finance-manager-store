package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.validate
import com.finitas.financemanagerstore.domain.services.UserService
import jakarta.validation.Valid
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

    @DeleteMapping("{idUser}/regular-spendings/{idRegularSpending}")
    fun deleteUserRegularSpendings(
        @PathVariable idUser: UUID,
        @PathVariable idRegularSpending: UUID
    ): ResponseEntity<Unit> {
        service.deleteUserRegularSpending(idUser = idUser, idRegularSpending = idRegularSpending)
        return ResponseEntity.noContent().build()
    }
}
