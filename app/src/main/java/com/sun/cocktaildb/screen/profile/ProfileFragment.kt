package com.sun.cocktaildb.screen.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.User
import com.sun.cocktaildb.data.repository.UserRepository
import com.sun.cocktaildb.data.repository.impl.FirebaseAuthImplement
import com.sun.cocktaildb.data.repository.impl.UserRepositoryImpl
import com.sun.cocktaildb.data.repository.remote.AuthRepository
import com.sun.cocktaildb.databinding.FragmentProfileBinding
import com.sun.cocktaildb.screen.authenticate.login.LoginActivity
import com.sun.cocktaildb.utils.base.BaseFragment
import com.sun.cocktaildb.utils.dialog.LoadingDialog
import com.sun.cocktaildb.utils.LanguageManager
import com.sun.cocktaildb.utils.LanguageUtils

/**
 * Profile Fragment for displaying and editing user profile information
 * Implements MVP pattern with Firebase Firestore integration
 */
class ProfileFragment :
    BaseFragment(),
    ProfileView {
    // Presenter and dependencies
    private lateinit var presenter: ProfilePresenter
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository
    private lateinit var loadingDialog: LoadingDialog

    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun initView() {
        // Initialize dependencies
        authRepository = FirebaseAuthImplement()
        userRepository = UserRepositoryImpl()
        presenter = ProfilePresenter(authRepository, userRepository)
        presenter.setView(this)

        // Initialize UI components
        initializeViews()
        setupClickListeners()

        // Load user profile
        presenter.loadUserProfile()
        
        // Update language display
        updateLanguageDisplay()
    }

    /**
     * Initialize UI components
     */
    private fun initializeViews() {
        loadingDialog = LoadingDialog(requireContext())
    }

    private fun setupClickListeners() {
        binding.tvEditProfile.setOnClickListener {
            presenter.toggleEditMode()
        }

        binding.btnLogout.setOnClickListener {
            presenter.logout()
        }
        
        binding.tvLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        // Add text change listeners to update presenter when fields change
        binding.etName.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}

                override fun afterTextChanged(s: android.text.Editable?) {
                    if (presenter.isInEditMode()) {
                        presenter.updateProfileFields(
                            s.toString(),
                            binding.etPhone.text.toString(),
                        )
                    }
                }
            },
        )

        binding.etPhone.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}

                override fun afterTextChanged(s: android.text.Editable?) {
                    if (presenter.isInEditMode()) {
                        presenter.updateProfileFields(
                            binding.etName.text.toString(),
                            s.toString(),
                        )
                    }
                }
            },
        )
    }

    override fun displayUserProfile(user: User) {
        // Display user information in UI fields
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.phoneNumber)
    }

    override fun setEditMode(isEditMode: Boolean) {
        // Enable/disable editing of profile fields
        binding.etName.isEnabled = isEditMode
        binding.etPhone.isEnabled = isEditMode

        // Update edit profile text
        binding.tvEditProfile.text = if (isEditMode) getString(R.string.save) else getString(R.string.edit_profile)

        // Show visual feedback
        if (isEditMode) {
            binding.etName.requestFocus()
        }
    }

    override fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    override fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.hide()
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToLogin() {
        // Navigate to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    
    /**
     * Show language selection dialog
     */
    private fun showLanguageSelectionDialog() {
        val currentLanguage = LanguageManager.getCurrentLanguage(requireContext())
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_vietnamese)
        )
        
        val checkedItem = when (currentLanguage) {
            LanguageManager.LANGUAGE_ENGLISH -> 0
            LanguageManager.LANGUAGE_VIETNAMESE -> 1
            else -> 0
        }
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.language))
            .setSingleChoiceItems(languages, checkedItem) { _, which ->
                val selectedLanguage = when (which) {
                    0 -> LanguageManager.LANGUAGE_ENGLISH
                    1 -> LanguageManager.LANGUAGE_VIETNAMESE
                    else -> LanguageManager.LANGUAGE_ENGLISH
                }
                
                if (selectedLanguage != currentLanguage) {
                    changeLanguage(selectedLanguage)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Change app language
     */
    private fun changeLanguage(languageCode: String) {
        LanguageManager.setLanguage(requireContext(), languageCode)
        
        // Show restart dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.language_changed))
            .setMessage(getString(R.string.restart_app_message))
            .setPositiveButton(getString(R.string.restart_app)) { _, _ ->
                // Restart the app using LanguageUtils
                LanguageUtils.restartApp(requireActivity())
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(false)
            .show()
    }
    
    /**
     * Update language display text
     */
    private fun updateLanguageDisplay() {
        val currentLanguage = LanguageManager.getCurrentLanguage(requireContext())
        val languageText = LanguageManager.getLanguageDisplayName(requireContext(), currentLanguage)
        binding.tvLanguage.text = languageText
    }
}
