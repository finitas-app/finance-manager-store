package com.finitas.financemanagerstore.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class MoneySerializer : JsonSerializer<BigDecimal>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: BigDecimal, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeString(value.setScale(2, RoundingMode.HALF_UP).toString())
    }
}