package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.domain.services.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(private val service: UserService) {

    @PutMapping
    fun upsertUser(@Valid @RequestBody request: UserDto): ResponseMessage {
        service.upsertUser(request)
        return ResponseMessage("success")
    }

    @PostMapping("{idUser}/regular-spendings")
    fun addRegularSpendings(
        @PathVariable idUser: String,
        @Valid @RequestBody regularSpendings: List<RegularSpendingDto>
    ): ResponseMessage {
        service.addNewRegularSpendings(idUser, regularSpendings)
        return ResponseMessage("success")
    }

    @GetMapping("nicknames")
    fun getNicknames(@Valid @RequestBody request: GetVisibleNamesRequest): List<IdUserWithVisibleName> {
        return service.getVisibleNames(request)
    }
}
