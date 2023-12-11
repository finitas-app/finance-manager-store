package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.validate
import com.finitas.financemanagerstore.domain.services.UserService
import jakarta.validation.Valid
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(private val service: UserService) {

    @PutMapping
    fun upsertUser(@Valid @RequestBody request: UserDto, errors: Errors): ResponseMessage {
        errors.validate()
        service.upsertUser(request)
        return ResponseMessage("success")
    }

    @PostMapping("{idUser}/regular-spendings")
    fun addRegularSpendings(
        @PathVariable idUser: String,
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
}
