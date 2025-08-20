package com.sun.cocktaildb.data.repository

interface AuthRepository {
    fun register(
        email: String,
        password: String,
        callback: (Result<Unit>) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Result<Unit>) -> Unit
    )

    fun logout()

    fun currentUserId(): String?
}
