package com.sun.cocktaildb.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.text.TextUtils
import android.view.View
import java.util.*

/**
 * Utility class for language-related operations
 */
object LanguageUtils {
    private const val TAG = "LanguageUtils"
    
    /**
     * Refresh activity to apply new language
     */
    fun refreshActivity(activity: Activity) {
        try {
            // Recreate the activity to apply new language
            activity.recreate()
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing activity: ${e.message}")
        }
    }
    
    /**
     * Restart app to apply language changes
     */
    fun restartApp(activity: Activity) {
        try {
            val intent = activity.intent
            activity.finish()
            activity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error restarting app: ${e.message}")
        }
    }
    
    /**
     * Get localized string with current language
     */
    fun getLocalizedString(context: Context, stringResId: Int): String {
        return try {
            val languageCode = LanguageManager.getCurrentLanguage(context)
            val locale = Locale(languageCode)
            val config = Configuration(context.resources.configuration)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale)
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
            }
            
            val localizedContext = context.createConfigurationContext(config)
            localizedContext.getString(stringResId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting localized string: ${e.message}")
            context.getString(stringResId)
        }
    }
    
    /**
     * Check if current language is RTL (Right-to-Left)
     */
    fun isRTL(context: Context): Boolean {
        val languageCode = LanguageManager.getCurrentLanguage(context)
        val locale = Locale(languageCode)
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL
    }
}
