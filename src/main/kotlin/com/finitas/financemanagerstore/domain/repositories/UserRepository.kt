package com.finitas.financemanagerstore.domain.repositories

import com.finitas.financemanagerstore.domain.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>