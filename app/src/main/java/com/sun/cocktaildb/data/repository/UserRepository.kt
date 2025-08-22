package com.sun.cocktaildb.data.repository

import com.sun.cocktaildb.data.model.User

/**
 * Repository interface for user profile operations
 * Handles CRUD operations for user data in Firestore
 */
interface UserRepository {
    
    /**
     * Get user profile by user ID
     * @param userId Firebase Auth UID
     * @param callback Result callback with User object or error
     */
    fun getUserProfile(userId: String, callback: (Result<User>) -> Unit)
    
    /**
     * Create or update user profile
     * @param user User object to save
     * @param callback Result callback with success/error
     */
    fun saveUserProfile(user: User, callback: (Result<Unit>) -> Unit)
    
    /**
     * Update specific user fields
     * @param userId Firebase Auth UID
     * @param updates Map of field updates
     * @param callback Result callback with success/error
     */
    fun updateUserFields(userId: String, updates: Map<String, Any>, callback: (Result<Unit>) -> Unit)
    
    /**
     * Delete user profile
     * @param userId Firebase Auth UID
     * @param callback Result callback with success/error
     */
    fun deleteUserProfile(userId: String, callback: (Result<Unit>) -> Unit)
} 