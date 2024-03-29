package com.finitas.financemanagerstore.api.controllers

import com.finitas.financemanagerstore.api.dto.*
import com.finitas.financemanagerstore.config.validate
import com.finitas.financemanagerstore.domain.services.FinishedSpendingService
import jakarta.validation.Valid
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/store/finished-spendings")
class FinishedSpendingController(private val service: FinishedSpendingService) {

    @PutMapping
    fun updateWithChangedItems(
        @Valid @RequestBody request: IdUserWithEntities<FinishedSpendingDto>,
        errors: Errors
    ) {
        errors.validate()
        service.updateWithChangedItems(request)
    }

    @GetMapping
    fun fetchUsersUpdates(@RequestBody request: List<IdUserWithVersion>): List<FetchUpdatesResponse<FinishedSpendingDto>> {
        return service.fetchUsersUpdates(request)
    }

    @GetMapping("{idUser}")
    fun getUserFinishedSpendings(@PathVariable idUser: UUID): List<FinishedSpendingDto> {
        return service.getAll(idUser)
    }

    @PostMapping
    fun insertShoppingList(@Valid @RequestBody dto: FinishedSpendingDto, errors: Errors): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.insert(dto)
        return UpdateResponse(lastSyncVersion)
    }

    @PatchMapping
    fun updateShoppingList(@Valid @RequestBody dto: FinishedSpendingDto, errors: Errors): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.update(dto)
        return UpdateResponse(lastSyncVersion)
    }

    @DeleteMapping
    fun deleteShoppingList(
        @Valid @RequestBody request: DeleteFinishedSpendingRequest,
        errors: Errors
    ): UpdateResponse {
        errors.validate()
        val lastSyncVersion = service.delete(idUser = request.idUser, idSpendingSummary = request.idSpendingSummary)
        return UpdateResponse(lastSyncVersion)
    }
}

