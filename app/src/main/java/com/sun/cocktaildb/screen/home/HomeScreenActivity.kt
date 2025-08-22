package com.sun.cocktaildb.screen.home

import androidx.fragment.app.Fragment
import com.sun.cocktaildb.R
import com.sun.cocktaildb.databinding.ActivityHomeScreenBinding
import com.sun.cocktaildb.screen.favorite.FavoriteFragment
import com.sun.cocktaildb.screen.profile.ProfileFragment
import com.sun.cocktaildb.screen.search.SearchFragment
import com.sun.cocktaildb.utils.base.BaseActivity



class HomeScreenActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    private val homeFragment by lazy { HomeFragment() }
    private val favoritesFragment by lazy { FavoriteFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private val profileFragment by lazy { ProfileFragment() }
    private var activeFragment: Fragment? = null

    override fun initView() {
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        activeFragment = homeFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
            .add(R.id.fragment_container, searchFragment, "search").hide(searchFragment)
            .add(R.id.fragment_container, favoritesFragment, "favorites").hide(favoritesFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    showFragment(homeFragment)
                    true
                }
                R.id.navigation_favorites -> {
                    showFragment(favoritesFragment)
                    true
                }
                R.id.navigation_search -> {
                    showFragment(searchFragment)
                    true
                }
                R.id.navigation_profile -> {
                    showFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(target: Fragment) {
        if (activeFragment === target) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment ?: target)
            .show(target)
            .commit()
        activeFragment = target
    }
}
