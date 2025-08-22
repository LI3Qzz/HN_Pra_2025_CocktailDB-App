package com.sun.cocktaildb.screen.profile

import com.sun.cocktaildb.data.model.User
import com.sun.cocktaildb.data.repository.UserRepository
import com.sun.cocktaildb.data.repository.remote.AuthRepository
import com.sun.cocktaildb.utils.base.BasePresenter

/**
 * Presenter for Profile screen
 * Handles business logic for user profile management
 */
class ProfilePresenter(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : BasePresenter<ProfileView> {
    private var currentUser: User? = null
    private var isEditMode = false

    private var view: ProfileView? = null

    /**
     * Load user profile from Firestore
     */
    fun loadUserProfile() {
        val userId = authRepository.currentUserId()
        if (userId != null) {
            view?.showLoading(true)
            userRepository.getUserProfile(userId) { result ->
                result.fold(
                    onSuccess = { user ->
                        currentUser = user
                        view?.showLoading(false)
                        view?.displayUserProfile(user)
                    },
                    onFailure = { exception ->
                        view?.showLoading(false)
                        view?.showError("Failed to load profile: ${exception.message}")
                    },
                )
            }
        } else {
            view?.showError("User not authenticated")
        }
    }

    /**
     * Toggle edit mode for profile fields
     */
    fun toggleEditMode() {
        isEditMode = !isEditMode
        view?.setEditMode(isEditMode)

        if (!isEditMode) {
            // Save changes when exiting edit mode
            saveProfileChanges()
        }
    }

    /**
     * Save profile changes to Firestore
     */
    private fun saveProfileChanges() {
        currentUser?.let { user ->
            view?.showLoading(true)
            userRepository.saveUserProfile(user) { result ->
                result.fold(
                    onSuccess = {
                        view?.showLoading(false)
                        view?.showSuccess("Profile updated successfully")
                    },
                    onFailure = { exception ->
                        view?.showLoading(false)
                        view?.showError("Failed to update profile: ${exception.message}")
                    },
                )
            }
        }
    }

    /**
     * Update user profile fields
     */
    fun updateProfileFields(
        name: String,
        phoneNumber: String,
    ) {
        currentUser =
            currentUser?.copy(
                name = name,
                phoneNumber = phoneNumber,
            )
    }

    /**
     * Logout current user
     */
    fun logout() {
        authRepository.logout()
        view?.navigateToLogin()
    }

    /**
     * Check if user is in edit mode
     */
    fun isInEditMode(): Boolean = isEditMode

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun setView(view: ProfileView?) {
        this.view = view
    }
}
