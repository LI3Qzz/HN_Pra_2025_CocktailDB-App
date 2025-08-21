package com.sun.cocktaildb.screen.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sun.cocktaildb.utils.base.BaseFragment
import com.sun.cocktaildb.R

class FavoritesFragment : BaseFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(context).apply {
            text = "Favorites Fragment - Triển khai sau"
            textSize = 18f
            setTextColor(resources.getColor(R.color.black, null))
            gravity = android.view.Gravity.CENTER
        }
    }
    
    override fun initView() {
        // TODO: Initialize favorites functionality
    }
}
