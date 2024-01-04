package com.finitas.financemanagerstore.domain.services

import org.springframework.data.mongodb.core.query.Update
import kotlin.reflect.KProperty


@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun <@kotlin.internal.OnlyInputTypes T> Update.set(property: KProperty<T>, data: T): Update {
    return set(property.name, data)
}