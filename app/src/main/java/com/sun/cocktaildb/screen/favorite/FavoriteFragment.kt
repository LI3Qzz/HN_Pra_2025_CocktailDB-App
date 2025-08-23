package com.sun.cocktaildb.screen.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.FragmentFavoriteBinding
import com.sun.cocktaildb.screen.cocktaildetail.CocktailActivity
import com.sun.cocktaildb.screen.favorite.adapter.FavoriteAdapter
import com.sun.cocktaildb.utils.FavoriteSyncManager
import com.sun.cocktaildb.utils.base.BaseFragment
import com.sun.cocktaildb.utils.dialog.LoadingDialog

class FavoriteFragment : BaseFragment(), FavoriteView, FavoriteSyncManager.FavoriteUpdateListener {
    private val binding: FragmentFavoriteBinding by lazy {
        FragmentFavoriteBinding.inflate(layoutInflater)
    }
    private lateinit var presenter: FavoritePresenter
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var needsRefresh = false

    private val loadingDialog by lazy {
        LoadingDialog(this@FavoriteFragment.requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun initView() {
        setupPresenter()
        setupRecyclerView()
        // Register for favorite updates
        FavoriteSyncManager.registerListener(this)
    }

    private fun setupPresenter() {
        presenter = FavoritePresenter(CocktailRepositoryImpl())
        presenter.setView(this)
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(
            onCocktailClickListener = { cocktail ->
                val intent = CocktailActivity.newIntent(requireContext(), cocktail.id)
                startActivity(intent)
            },
            onFavoriteClickListener = { cocktail, isFavorite ->
                onFavoriteClicked(cocktail, isFavorite)
            }
        )
        binding.rvFavorites.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = favoriteAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        // Always refresh on resume to ensure up-to-date favorites list
        presenter.onStart()
        needsRefresh = false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            // Refresh when fragment becomes visible (for ViewPager)
            if (needsRefresh) {
                refreshFavoritesList()
                needsRefresh = false
            } else {
                presenter.onStart()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isResumed) {
            // Refresh when fragment becomes visible (for FragmentTransaction show/hide)
            if (needsRefresh) {
                refreshFavoritesList()
                needsRefresh = false
            } else {
                presenter.onStart()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister from favorite updates
        FavoriteSyncManager.unregisterListener(this)
    }

    // FavoriteSyncManager.FavoriteUpdateListener implementations
    override fun onFavoriteUpdated(cocktailId: String, isFavorite: Boolean) {
        if (isFavorite) {
            // If a cocktail was added to favorites from another screen
            needsRefresh = true
            
            // If fragment is visible and resumed, refresh immediately
            if (isVisible && isResumed) {
                refreshFavoritesList()
                needsRefresh = false
            }
        } else {
            // If a cocktail was removed from favorites, refresh immediately
            presenter.onStart()
        }
    }

    override fun onFavoritesRefreshed() {
        // Refresh the entire favorites list when any screen updates favorites
        // This ensures the favorites list is always up to date
        presenter.onStart()
    }

    // Method to immediately refresh favorites list
    private fun refreshFavoritesList() {
        // Get current favorites and check if the new cocktail is already in the list
        val currentFavorites = favoriteAdapter.getCurrentCocktails()
        
        // If the list is empty, refresh from presenter
        if (currentFavorites.isEmpty()) {
            presenter.onStart()
        } else {
            // Check if we need to add the new cocktail to the list
            // This is a quick refresh without going to the database
            presenter.onStart()
        }
    }

    // FavoriteView implementations
    override fun showFavorites(items: List<Cocktail>) {
        if (items.isNotEmpty()) {
            binding.emptyStateContainer.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
            favoriteAdapter.updateCocktails(items)
        } else {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        }
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.hide()
    }

    override fun showError(message: String) {
        // Show error message
    }

    // Handle favorite toggle
    private fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        if (!isFavorite) {
            // Remove from favorites using FavoriteSyncManager to notify all screens
            FavoriteSyncManager.updateFavorite(cocktail, false)
            
            // Immediately remove from local list for better UX
            val currentItems = favoriteAdapter.getCurrentCocktails().toMutableList()
            currentItems.removeAll { it.id == cocktail.id }
            favoriteAdapter.updateCocktails(currentItems)
            
            // Show empty state if no more favorites
            if (currentItems.isEmpty()) {
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            }
        }
    }
}


