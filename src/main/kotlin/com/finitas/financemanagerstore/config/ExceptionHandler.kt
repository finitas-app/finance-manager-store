package com.finitas.financemanagerstore.config

import com.finitas.financemanagerstore.api.dto.ResponseMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [BaseException::class])
    protected fun handleException(exception: BaseException, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity(ResponseMessage(exception.message), exception.statusCode)
    }
}