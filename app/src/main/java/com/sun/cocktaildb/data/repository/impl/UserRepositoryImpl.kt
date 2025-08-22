package com.sun.cocktaildb.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sun.cocktaildb.data.model.User
import com.sun.cocktaildb.data.repository.UserRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Implementation of UserRepository using Firebase Firestore
 * Manages user profile data in the /users collection
 */
class UserRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override fun getUserProfile(userId: String, callback: (Result<User>) -> Unit) {
        executor.execute {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Document exists, convert to User object
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            callback(Result.success(user))
                        } else {
                            callback(Result.failure(Exception("Failed to parse user data")))
                        }
                    } else {
                        // Document doesn't exist, return empty user
                        callback(Result.success(User(userId = userId)))
                    }
                }
                .addOnFailureListener { exception ->
                    callback(Result.failure(exception))
                }
        }
    }

    override fun saveUserProfile(user: User, callback: (Result<Unit>) -> Unit) {
        executor.execute {
            // Update timestamp before saving
            val userToSave = user.copy(updatedAt = System.currentTimeMillis())
            
            firestore.collection(USERS_COLLECTION)
                .document(user.userId)
                .set(userToSave, SetOptions.merge())
                .addOnSuccessListener {
                    callback(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    callback(Result.failure(exception))
                }
        }
    }

    override fun updateUserFields(userId: String, updates: Map<String, Any>, callback: (Result<Unit>) -> Unit) {
        executor.execute {
            // Add updated timestamp
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updatedAt"] = System.currentTimeMillis()
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updatesWithTimestamp)
                .addOnSuccessListener {
                    callback(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    callback(Result.failure(exception))
                }
        }
    }

    override fun deleteUserProfile(userId: String, callback: (Result<Unit>) -> Unit) {
        executor.execute {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .addOnSuccessListener {
                    callback(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    callback(Result.failure(exception))
                }
        }
    }
} 