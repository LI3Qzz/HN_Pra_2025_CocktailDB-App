package com.sun.cocktaildb.screen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sun.cocktaildb.R
import com.sun.cocktaildb.utils.base.BaseFragment

class ProfileFragment : BaseFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(context).apply {
            text = "Profile Fragment - Triển khai sau"
            textSize = 18f
            setTextColor(resources.getColor(R.color.black, null))
            gravity = android.view.Gravity.CENTER
        }
    }
    
    override fun initView() {
        // TODO: Initialize profile functionality
    }
}
