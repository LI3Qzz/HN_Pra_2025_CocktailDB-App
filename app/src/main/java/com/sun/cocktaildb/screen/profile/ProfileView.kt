package com.sun.cocktaildb.screen.profile

import com.sun.cocktaildb.data.model.User
import com.sun.cocktaildb.utils.base.BaseView

/**
 * View interface for Profile screen
 * Defines UI operations that the presenter can call
 */
interface ProfileView : BaseView {
    /**
     * Display user profile information
     * @param user User object containing profile data
     */
    fun displayUserProfile(user: User)

    /**
     * Set edit mode for profile fields
     * @param isEditMode true to enable editing, false to disable
     */
    fun setEditMode(isEditMode: Boolean)

    /**
     * Show loading state
     * @param isLoading true to show loading, false to hide
     */
    fun showLoading(isLoading: Boolean)

    /**
     * Show success message
     * @param message Success message to display
     */
    fun showSuccess(message: String)

    /**
     * Show error message
     * @param message Error message to display
     */
    override fun showError(message: String)

    /**
     * Navigate to login screen after logout
     */
    fun navigateToLogin()
} 
