package com.sun.cocktaildb.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import java.util.*

/**
 * Language Manager for handling app language changes
 * Supports English and Vietnamese languages
 */
object LanguageManager {
    private const val TAG = "LanguageManager"
    private const val PREF_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    
    // Language constants
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_VIETNAMESE = "vi"
    
    /**
     * Get current language from SharedPreferences
     */
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
    }
    
    /**
     * Set language and save to SharedPreferences
     */
    fun setLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
        
        Log.d(TAG, "Language set to: $languageCode")
    }
    
    /**
     * Apply language to the context
     */
    fun applyLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
    
    /**
     * Get localized resources for the current language
     */
    fun getLocalizedResources(context: Context): Resources {
        val languageCode = getCurrentLanguage(context)
        val locale = Locale(languageCode)
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        return context.createConfigurationContext(config).resources
    }
    
    /**
     * Check if current language is Vietnamese
     */
    fun isVietnamese(context: Context): Boolean {
        return getCurrentLanguage(context) == LANGUAGE_VIETNAMESE
    }
    
    /**
     * Check if current language is English
     */
    fun isEnglish(context: Context): Boolean {
        return getCurrentLanguage(context) == LANGUAGE_ENGLISH
    }
    
    /**
     * Get display name for language code
     */
    fun getLanguageDisplayName(context: Context, languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_ENGLISH -> "English"
            LANGUAGE_VIETNAMESE -> "Tiếng Việt"
            else -> languageCode
        }
    }
    
    /**
     * Get current language display name
     */
    fun getCurrentLanguageDisplayName(context: Context): String {
        val languageCode = getCurrentLanguage(context)
        return getLanguageDisplayName(context, languageCode)
    }
}
