package com.finitas.financemanagerstore.config

import com.finitas.financemanagerstore.api.dto.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class RestResponseEntityExceptionHandler {
    @ExceptionHandler(value = [BaseException::class])
    fun handleException(exception: BaseException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(exception.errorCode, exception.errorMessage), exception.statusCode)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun handleException(
        exception: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        exception.printStackTrace()
        return ResponseEntity(
            ErrorResponse(errorCode = ErrorCode.STORE_REQUEST_NON_PARSABLE, "Request non parsable"),
            HttpStatus.BAD_REQUEST
        )
    }
}