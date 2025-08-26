package com.sun.cocktaildb

import android.app.Application
import android.content.Context
import com.sun.cocktaildb.utils.LanguageManager

/**
 * Application class for CocktailDB app
 * Handles app-wide initialization including language setup
 */
class CocktailApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize language manager
        initializeLanguage()
    }
    
    override fun attachBaseContext(base: Context) {
        // Apply saved language when attaching base context
        val languageCode = LanguageManager.getCurrentLanguage(base)
        val locale = java.util.Locale(languageCode)
        val config = base.resources.configuration
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        val context = base.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
    
    /**
     * Initialize language settings
     */
    private fun initializeLanguage() {
        try {
            // Apply saved language to application context
            val languageCode = LanguageManager.getCurrentLanguage(this)
            LanguageManager.applyLanguage(this, languageCode)
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }
}
