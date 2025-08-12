package com.sun.cocktaildb.screen.splashscreen

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.sun.cocktaildb.R
import com.sun.cocktaildb.screen.MainActivity
import com.sun.cocktaildb.screen.authenticate.LoginActivity
import com.sun.cocktaildb.utils.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : BaseActivity() {
    override fun getLayoutResourceId(): Int = R.layout.activity_splashscreen

    override fun initView() {
        lifecycleScope.launch {
            delay(1000)
            nextActivity()
        }
    }

    private fun nextActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }
}
