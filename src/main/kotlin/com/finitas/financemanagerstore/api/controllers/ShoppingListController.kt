package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.validate
import com.finitas.financemanagerstore.domain.services.ShoppingListService
import jakarta.validation.Valid
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/store/shopping-lists")
class ShoppingListController(private val service: ShoppingListService) {

    @PutMapping("synchronize")
    fun synchronize(
        @Valid @RequestBody request: SynchronizationRequest<ShoppingListDto>,
        errors: Errors
    ): SynchronizationResponse<ShoppingListDto> {
        errors.validate()
        return service.synchronize(request)
    }

    @GetMapping("{idUser}")
    fun getUserShoppingLists(@PathVariable idUser: String): List<ShoppingListDto> {
        return service.getAll(idUser)
    }

    @PostMapping
    fun insertShoppingList(@Valid @RequestBody dto: ShoppingListDto, errors: Errors): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.insert(dto)
        return UpdateResponse(lastSyncVersion)
    }

    @PatchMapping
    fun updateShoppingList(@Valid @RequestBody dto: ShoppingListDto, errors: Errors): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.update(dto)
        return UpdateResponse(lastSyncVersion)
    }

    @DeleteMapping
    fun deleteShoppingList(@Valid @RequestBody request: DeleteShoppingListRequest, errors: Errors): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.delete(idUser = request.idUser, idShoppingList = request.idShoppingList)
        return UpdateResponse(lastSyncVersion)
    }
}
