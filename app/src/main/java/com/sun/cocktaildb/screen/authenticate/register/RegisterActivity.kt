package com.sun.cocktaildb.screen.authenticate.register

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.sun.cocktaildb.databinding.ActivityRegisterBinding
import com.sun.cocktaildb.screen.authenticate.login.LoginActivity
import com.sun.cocktaildb.utils.base.BaseActivity
import com.sun.cocktaildb.utils.dialog.LoadingDialog

class RegisterActivity :
    BaseActivity(),
    RegisterContract.View {
    private lateinit var binding: ActivityRegisterBinding
    private val presenter by lazy {
        RegisterPresenter()
    }
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun initView() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
        presenter.setView(this)
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.hide()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmailInvalid(message: String) {
        binding.emailError.visibility = View.VISIBLE
        binding.emailError.text = message
    }

    override fun showPasswordInvalid(message: String) {
        binding.passwordError.visibility = View.VISIBLE
        binding.passwordError.text = message
    }

    override fun showConfirmPasswordInvalid(message: String) {
        binding.passwordConfirmError.visibility = View.VISIBLE
        binding.passwordConfirmError.text = message
    }

    override fun onRegisterSuccess() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    override fun onRegisterFailed(message: String) {
        showError(message)
    }

    fun initEvent() {
        binding.btnRegister.setOnClickListener {
            binding.emailError.visibility = View.GONE
            binding.passwordError.visibility = View.GONE
            binding.passwordConfirmError.visibility = View.GONE
            if (presenter.validateInputs(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etPasswordConfirm.text.toString(),
                )
            ) {
                presenter.doRegister(
                    binding.etUsername.text.toString(),
                    binding.etPassword.text.toString(),
                )
            }
        }
        binding.etUsername.setOnClickListener {
            binding.emailError.visibility = View.GONE
        }
        binding.etPassword.setOnClickListener {
            binding.passwordError.visibility = View.GONE
        }
        binding.etPasswordConfirm.setOnClickListener {
            binding.passwordConfirmError.visibility = View.GONE
        }
    }
}
