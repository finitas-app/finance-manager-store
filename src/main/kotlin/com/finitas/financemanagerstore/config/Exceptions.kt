package com.finitas.financemanagerstore.config

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

open class BaseException(
    override val message: String,
    cause: Exception? = null,
    val statusCode: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
) : Exception(message, cause)

class NotFoundException(
    message: String = "Not found",
    cause: Exception? = null,
) : BaseException(message, cause, HttpStatus.NOT_FOUND)

class BadRequestException(
    message: String = "Bad request",
    cause: Exception? = null,
) : BaseException(message, cause, HttpStatus.BAD_REQUEST)

class UnauthorizedException(
    message: String = "Unauthorized",
    cause: Exception? = null,
) : BaseException(message, cause, HttpStatus.UNAUTHORIZED)

class ConflictException(
    message: String = "Conflict",
    cause: Exception? = null,
) : BaseException(message, cause, HttpStatus.CONFLICT)

class InternalServerException(
    message: String = "Internal Server Error",
    cause: Exception? = null,
) : BaseException(message, cause)
