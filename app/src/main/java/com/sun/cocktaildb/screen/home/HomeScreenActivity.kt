package com.sun.cocktaildb.screen.home

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sun.cocktaildb.R
import com.sun.cocktaildb.screen.favorites.FavoritesFragment
import com.sun.cocktaildb.screen.profile.ProfileFragment
import com.sun.cocktaildb.screen.search.SearchFragment
import com.sun.cocktaildb.utils.base.BaseActivity
import com.sun.cocktaildb.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : BaseActivity() {
    
    private lateinit var binding: ActivityHomeScreenBinding
    
    override fun initView() {
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        // Set default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_favorites -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.navigation_search -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
