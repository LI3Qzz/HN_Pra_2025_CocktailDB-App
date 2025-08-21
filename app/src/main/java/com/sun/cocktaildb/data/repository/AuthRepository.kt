package com.sun.cocktaildb.data.repository

interface AuthRepository {
    fun register(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit,
    )

    fun login(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit,
    )

    fun logout()

    fun currentUserId(): String?
}
