package com.sun.cocktaildb.screen.home

import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.ActivityHomeScreenBinding
import com.sun.cocktaildb.screen.home.adapter.CategoryAdapter
import com.sun.cocktaildb.screen.home.adapter.PopularCocktailAdapter
import com.sun.cocktaildb.screen.categorydetail.CategoryDetailActivity
import com.sun.cocktaildb.screen.search.SearchActivity
import com.sun.cocktaildb.utils.base.BaseActivity
import com.sun.cocktaildb.utils.FavoriteManager

class HomeScreenActivity : BaseActivity(), HomeView {
    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var presenter: HomePresenter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularCocktailAdapter: PopularCocktailAdapter

    override fun initView() {
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPresenter()
        setupRecyclerViews()
        setupBottomNavigation()
    }

    private fun setupPresenter() {
        presenter = HomePresenter(CocktailRepositoryImpl())
        presenter.setView(this)
    }

    private fun setupRecyclerViews() {
        // Setup Categories RecyclerView
        categoryAdapter =
            CategoryAdapter { category ->
                presenter.onCategoryClicked(category)
            }
        binding.rvCategories.apply {
            layoutManager =
                LinearLayoutManager(this@HomeScreenActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Setup Popular Cocktails RecyclerView
        popularCocktailAdapter =
            PopularCocktailAdapter(
                onCocktailClickListener = { cocktail ->
                    presenter.onCocktailClicked(cocktail)
                },
                onFavoriteClickListener = { cocktail, isFavorite ->
                    presenter.onFavoriteClicked(cocktail, isFavorite)
                }
            )
        binding.rvPopular.apply {
            layoutManager = GridLayoutManager(this@HomeScreenActivity, 2)
            adapter = popularCocktailAdapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            presenter.onBottomNavigationItemSelected(menuItem.itemId)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onStart()
    }

    override fun onPause() {
        super.onPause()
        presenter.onStop()
    }

    // HomeScreenView implementations
    override fun showCategories(categories: List<Category>) {
        categoryAdapter.updateCategories(categories)
    }

    override fun showPopularCocktails(cocktails: List<Cocktail>) {
        popularCocktailAdapter.updateCocktails(cocktails)
    }

    override fun showLoading() {
        // Loading indicator
    }

    override fun hideLoading() {
        // Loading indicator
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCategoryClicked(category: Category) {

        val intent = CategoryDetailActivity.newIntent(this, category)
        startActivity(intent)

        Toast.makeText(this, "Navigate to CategoryScreen for: ${category.name}", Toast.LENGTH_SHORT).show()

    }

    override fun onCocktailClicked(cocktail: Cocktail) {
        Toast.makeText(this, getString(R.string.navigate_to_detail_screen, cocktail.name), Toast.LENGTH_SHORT).show()
        // TODO: Navigate to detail screen later
    }
    
    override fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        if (isFavorite) {
            Toast.makeText(this, getString(R.string.added_to_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.removed_from_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        }
        
        // Update the cocktail favorite status in the adapter
        popularCocktailAdapter.updateCocktailFavoriteStatus(cocktail.id, isFavorite)
        
        // Update favorite status in FavoriteManager
        if (isFavorite) {
            FavoriteManager.addToFavorites(cocktail)
        } else {
            FavoriteManager.removeFromFavorites(cocktail)
        }
        
        // Update favorites count in bottom navigation if available
        updateFavoritesCount()
    }
    
    private fun updateFavoritesCount() {
        val favoritesCount = FavoriteManager.getFavoriteCocktails().size
        // TODO: Update badge count in bottom navigation if supported
    }

    override fun onBottomNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.navigation_home -> {
                // Already on home screen
            }
            R.id.navigation_favorites -> {
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show()
                // Favorites navigation
            }
            R.id.navigation_search -> {
                val intent = SearchActivity.newIntent(this)
                startActivity(intent)
            }
            R.id.navigation_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                // Profile navigation
            }
        }
    }
}
