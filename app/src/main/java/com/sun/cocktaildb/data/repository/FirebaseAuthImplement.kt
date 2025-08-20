package com.sun.cocktaildb.data.repository

import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FirebaseAuthImplement(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : AuthRepository {
    
    override fun register(
        email: String,
        password: String,
        callback: (Result<Unit>) -> Unit
    ) {
        executor.execute {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        callback(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Result<Unit>) -> Unit
    ) {
        executor.execute {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(Result.success(Unit))
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
