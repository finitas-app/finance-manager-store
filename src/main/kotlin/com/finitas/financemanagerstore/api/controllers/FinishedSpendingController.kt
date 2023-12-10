package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.DeleteFinishedSpendingRequest
import com.finitas.financemanagerstore.api.dto.FinishedSpendingDto
import com.finitas.financemanagerstore.api.dto.SynchronizationRequest
import com.finitas.financemanagerstore.api.dto.SynchronizationResponse
import com.finitas.financemanagerstore.domain.services.FinishedSpendingService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/finished-spendings")
class FinishedSpendingController(private val service: FinishedSpendingService) {

    @PutMapping("synchronize")
    fun synchronize(@Valid @RequestBody request: SynchronizationRequest<FinishedSpendingDto>): SynchronizationResponse<FinishedSpendingDto> {
        return service.synchronize(request)
    }

    @PostMapping
    fun addShoppingList(@Valid @RequestBody dto: FinishedSpendingDto): FinishedSpendingDto {
        return service.addFinishedSpending(dto)
    }

    @PutMapping
    fun updateShoppingList(@Valid @RequestBody dto: FinishedSpendingDto): FinishedSpendingDto {
        return service.updateFinishedSpending(dto)
    }

    @DeleteMapping
    fun deleteShoppingList(@Valid @RequestBody request: DeleteFinishedSpendingRequest): FinishedSpendingDto {
        return service.deleteFinishedSpending(idUser = request.idUser, idSpendingSummary = request.idSpendingSummary)
    }
}

