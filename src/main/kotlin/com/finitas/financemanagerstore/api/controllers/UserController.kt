package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.domain.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(private val service: UserService) {

    @PutMapping
    fun upsertUser(@RequestBody request: UserDto): ResponseMessage {
        service.upsertUser(request)
        return ResponseMessage("success")
    }

    @PostMapping("{idUser}/regular-spendings")
    fun addRegularSpendings(
        @PathVariable idUser: String,
        @RequestBody regularSpendings: List<RegularSpendingDto>
    ): ResponseMessage {
        service.addNewRegularSpendings(idUser, regularSpendings)
        return ResponseMessage("success")
    }

    @GetMapping("nicknames")
    fun getNicknames(@RequestBody request: GetVisibleNamesRequest): List<IdUserWithVisibleName> {
        return service.getVisibleNames(request)
    }
}
