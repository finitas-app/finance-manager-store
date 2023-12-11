package com.finitas.financemanagerstore.config

import org.springframework.validation.Errors

fun Errors.validate() {
    if (!this.hasErrors()) return

    val message = allErrors.map { it.defaultMessage }.joinToString(", ")
    throw BadRequestException(message = message)
}