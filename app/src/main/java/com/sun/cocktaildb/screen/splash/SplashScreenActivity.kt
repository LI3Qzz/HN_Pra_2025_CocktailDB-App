package com.sun.cocktaildb.screen.splash

import android.content.Intent
import com.sun.cocktaildb.databinding.ActivitySplashscreenBinding
import com.sun.cocktaildb.screen.authenticate.login.LoginActivity
import com.sun.cocktaildb.screen.home.HomeScreenActivity
import com.sun.cocktaildb.utils.base.BaseActivity

class SplashScreenActivity :
    BaseActivity(),
    SplashView {
    private lateinit var binding: ActivitySplashscreenBinding
    private lateinit var presenter: SplashPresenter

    override fun initView() {
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPresenter()
    }

    private fun setupPresenter() {
        presenter = SplashPresenter()
        presenter.setView(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    // SplashView implementations
    override fun showLoading() {
        // Loading indicator
    }

    override fun hideLoading() {
        // Loading indicator
    }

    override fun showError(message: String) {
        // Error handling
    }

    override fun navigateToHome() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun navigateToLogin() {
        // Login navigation
        // For now, navigate to home
        navigateToHome()
    }
}
