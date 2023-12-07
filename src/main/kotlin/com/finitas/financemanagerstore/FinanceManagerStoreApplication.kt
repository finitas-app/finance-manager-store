package com.finitas.financemanagerstore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class FinanceManagerStoreApplication

fun main(args: Array<String>) {
    runApplication<FinanceManagerStoreApplication>(*args)
}
