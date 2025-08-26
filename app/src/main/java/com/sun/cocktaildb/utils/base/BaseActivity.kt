package com.sun.cocktaildb.utils.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sun.cocktaildb.utils.LanguageManager

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply saved language before initializing view
        applySavedLanguage()
        initView()
    }
    
    override fun attachBaseContext(newBase: Context) {
        // Apply language when attaching base context
        val languageCode = LanguageManager.getCurrentLanguage(newBase)
        val locale = java.util.Locale(languageCode)
        val config = newBase.resources.configuration
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
    
    private fun applySavedLanguage() {
        val languageCode = LanguageManager.getCurrentLanguage(this)
        LanguageManager.applyLanguage(this, languageCode)
    }

    abstract fun initView()
}
