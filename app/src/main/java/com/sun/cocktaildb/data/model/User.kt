package com.sun.cocktaildb.data.model

import com.google.firebase.firestore.DocumentId

/**
 * User data model for Firestore
 * Represents user profile information stored in the /users collection
 */
data class User(
    @DocumentId
    val userId: String = "", // Firebase Auth UID
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    // Empty constructor required for Firestore
    constructor() : this("", "", "", "", 0L, 0L)
}
