package com.sun.cocktaildb.utils.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sun.cocktaildb.utils.LanguageManager

abstract class BaseFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Apply saved language
        applySavedLanguage()
        initView()
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Apply language when attaching to context
        val languageCode = LanguageManager.getCurrentLanguage(context)
        val locale = java.util.Locale(languageCode)
        val config = context.resources.configuration
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        context.createConfigurationContext(config)
    }
    
    private fun applySavedLanguage() {
        context?.let { context ->
            val languageCode = LanguageManager.getCurrentLanguage(context)
            LanguageManager.applyLanguage(context, languageCode)
        }
    }
    
    abstract fun initView()
}
