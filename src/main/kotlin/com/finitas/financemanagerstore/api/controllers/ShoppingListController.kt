package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.ShoppingListDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.domain.services.ShoppingListService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/shopping-lists")
class ShoppingListController(private val service: ShoppingListService) {

    @PutMapping("synchronize")
    fun synchronize(@RequestBody request: SynchronizationRequest<ShoppingListDto>): SynchronizationResponse<ShoppingListDto> {
        return service.synchronize(request)
    }

    @PostMapping
    fun addShoppingList(@RequestBody dto: ShoppingListDto): ShoppingListDto {
        return service.addShoppingList(dto)
    }

    @PutMapping
    fun updateShoppingList(@RequestBody dto: ShoppingListDto): ShoppingListDto {
        return service.updateShoppingList(dto)
    }
}
