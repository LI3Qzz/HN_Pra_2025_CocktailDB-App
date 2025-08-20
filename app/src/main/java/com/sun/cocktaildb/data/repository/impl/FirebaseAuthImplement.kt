package com.sun.cocktaildb.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.sun.cocktaildb.data.repository.AuthRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FirebaseAuthImplement(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val executor: Executor = Executors.newSingleThreadExecutor(),
) : AuthRepository {
    override fun register(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit,
    ) {
        executor.execute {
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Result.success("Success"))
                    } else {
                        callback(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit,
    ) {
        executor.execute {
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Result.success("Success"))
                    } else {
                        callback(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun currentUserId(): String? = auth.currentUser?.uid
}
