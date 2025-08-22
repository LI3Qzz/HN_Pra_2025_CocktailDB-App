package com.sun.cocktaildb.screen.authenticate.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.sun.cocktaildb.R
import com.sun.cocktaildb.databinding.ActivityLoginBinding
import com.sun.cocktaildb.screen.authenticate.register.RegisterActivity
import com.sun.cocktaildb.screen.home.HomeScreenActivity
import com.sun.cocktaildb.utils.base.BaseActivity

class LoginActivity :
    BaseActivity(),
    LoginContract.View {
    private lateinit var presenter: LoginPresenter

    private var binding: ActivityLoginBinding? = null

    override fun initView() {
        presenter = LoginPresenter()
        presenter.setView(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
    }

    private fun setupClickListeners() {
        binding?.btnLogin?.setOnClickListener {
            if (validateInputs()) {
                presenter.login()
            }
        }

        binding?.tvSignup?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.setView(null)
    }

    override fun showLoading() {
        binding?.btnLogin?.isEnabled = false
        binding?.btnLogin?.text = getString(R.string.logging_in)
    }

    override fun hideLoading() {
        binding?.btnLogin?.isEnabled = true
        binding?.btnLogin?.text = getString(R.string.login)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess() {
        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
    }

    override fun navigateToMain() {
        val intent = Intent(this, HomeScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun getEmail(): String =
        binding
            ?.etUsername
            ?.text
            .toString()
            .trim()

    override fun getPassword(): String = binding?.etPassword?.text.toString()

    override fun clearInputs() {
        binding?.etUsername?.text?.clear()
        binding?.etPassword?.text?.clear()
    }

    fun validateInputs(): Boolean {
        val email = getEmail()
        val password = getPassword()

        if (email.isEmpty()) {
            showError(getString(R.string.email_required))
            return false
        }

        if (password.isEmpty()) {
            showError(getString(R.string.password_required))
            return false
        }
        return true
    }
}
