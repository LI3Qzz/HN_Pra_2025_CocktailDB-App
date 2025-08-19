package com.sun.cocktaildb.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sun.cocktaildb.utils.base.BaseFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.FragmentHomeBinding
import com.sun.cocktaildb.screen.home.adapter.CategoryAdapter
import com.sun.cocktaildb.screen.home.adapter.PopularCocktailAdapter
import com.sun.cocktaildb.screen.categorydetail.CategoryDetailActivity
import com.sun.cocktaildb.utils.FavoriteManager

class HomeFragment : BaseFragment(), HomeView {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var presenter: HomePresenter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularCocktailAdapter: PopularCocktailAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun initView() {
        setupPresenter()
        setupRecyclerViews()
    }
    
    private fun setupPresenter() {
        presenter = HomePresenter(CocktailRepositoryImpl())
        presenter.setView(this)
    }
    
    private fun setupRecyclerViews() {
        // Setup Categories RecyclerView
        categoryAdapter = CategoryAdapter { category ->
            presenter.onCategoryClicked(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Setup Popular Cocktails RecyclerView
        popularCocktailAdapter = PopularCocktailAdapter(
            onCocktailClickListener = { cocktail ->
                presenter.onCocktailClicked(cocktail)
            },
            onFavoriteClickListener = { cocktail, isFavorite ->
                presenter.onFavoriteClicked(cocktail, isFavorite)
            }
        )
        binding.rvPopular.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = popularCocktailAdapter
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    // HomeView implementations
    override fun showCategories(categories: List<Category>) {
        categoryAdapter.updateCategories(categories)
        println("DEBUG: Loaded ${categories.size} categories")
    }
    
    override fun showPopularCocktails(cocktails: List<Cocktail>) {
        popularCocktailAdapter.updateCocktails(cocktails)
        println("DEBUG: Loaded ${cocktails.size} popular cocktails")
    }
    
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }
    
    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
    
    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        println("DEBUG: Error: $message")
    }
    
    override fun onCategoryClicked(category: Category) {
        val intent = CategoryDetailActivity.newIntent(requireContext(), category)
        startActivity(intent)
        Toast.makeText(context, "Navigate to CategoryScreen for: ${category.name}", Toast.LENGTH_SHORT).show()
    }
    
    override fun onCocktailClicked(cocktail: Cocktail) {
        Toast.makeText(context, getString(R.string.navigate_to_detail_screen, cocktail.name), Toast.LENGTH_SHORT).show()
        // TODO: Navigate to detail screen later
    }
    
    override fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        if (isFavorite) {
            Toast.makeText(context, getString(R.string.added_to_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, getString(R.string.removed_from_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
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
        // Navigation is handled by Activity
    }
}
