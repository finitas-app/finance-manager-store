package com.finitas.financemanagerstore.config

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

open class BaseException(
    val errorMessage: String,
    val errorCode: ErrorCode,
    cause: Exception? = null,
    val statusCode: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
) : Exception(errorMessage, cause)

class NotFoundException(
    errorCode: ErrorCode,
    message: String = "Not found",
    cause: Exception? = null,
) : BaseException(message, errorCode, cause, HttpStatus.NOT_FOUND)

class BadRequestException(
    errorCode: ErrorCode,
    message: String = "Bad request",
    cause: Exception? = null,
) : BaseException(message, errorCode, cause, HttpStatus.BAD_REQUEST)

class ConflictException(
    errorCode: ErrorCode,
    message: String = "Conflict",
    cause: Exception? = null,
) : BaseException(message, errorCode, cause, HttpStatus.CONFLICT)
