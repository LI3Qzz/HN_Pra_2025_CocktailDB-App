package com.sun.cocktaildb.screen.home

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.remote.CocktailRepository
import com.sun.cocktaildb.utils.Constants
import com.sun.cocktaildb.utils.FavoriteManager
import com.sun.cocktaildb.utils.base.BasePresenter
import java.util.concurrent.Executors
import com.sun.cocktaildb.utils.FavoriteSyncManager

class HomePresenter(
    private val cocktailRepository: CocktailRepository,
) : BasePresenter<HomeView> {
    private var view: HomeView? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun setView(view: HomeView?) {
        this.view = view
    }

    override fun onStart() {
        loadCategories()
        loadPopularCocktails()
    }

    override fun onStop() {
        // Cleanup if needed
    }

    private fun loadCategories() {
        view?.showLoading()
        executor.execute {
            try {
                val categories = cocktailRepository.getCategories()
                mainHandler.post {
                    if (categories.isNotEmpty()) {
                        view?.showCategories(categories)
                    } else {
                        view?.showError("No categories found. Please check your internet connection.")
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.showError("${Constants.ERROR_LOADING_CATEGORIES}: ${e.message ?: Constants.UNKNOWN_ERROR}")
                }
            }
        }
    }

    private fun loadPopularCocktails() {
        executor.execute {
            try {
                // Load popular cocktails first
                val cocktails = cocktailRepository.getPopularCocktails()
                
                // Show cocktails immediately for better UX
                mainHandler.post {
                    view?.showPopularCocktails(cocktails)
                }
                
                // Then update favorite status in background
                cocktailRepository.getFavouriteCocktails { result ->
                    if (result.isSuccess) {
                        val favIds = result.getOrNull().orEmpty().map { it.id }.toSet()
                        val updatedCocktails = cocktails.map { it.copy(isFavorite = favIds.contains(it.id)) }
                        
                        mainHandler.post {
                            view?.showPopularCocktails(updatedCocktails)
                            view?.hideLoading()
                        }
                    } else {
                        // If Firebase fails, still show cocktails with local favorite status
                        mainHandler.post {
                            view?.hideLoading()
                        }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.showError("${Constants.ERROR_LOADING_POPULAR_COCKTAILS}: ${e.message ?: Constants.UNKNOWN_ERROR}")
                    view?.hideLoading()
                }
            }
        }
    }

    fun onCategoryClicked(category: Category) {
        view?.onCategoryClicked(category)
    }

    fun onCocktailClicked(cocktail: Cocktail) {
        view?.onCocktailClicked(cocktail)
    }

    fun onFavoriteClicked(
        cocktail: Cocktail,
        isFavorite: Boolean,
    ) {
        // Use FavoriteSyncManager to handle all favorite operations
        // This will automatically update Firebase and notify all screens
        FavoriteSyncManager.updateFavorite(cocktail, isFavorite)

        // No need to call loadPopularCocktails() here as FavoriteSyncManager will notify HomeFragment
        // This prevents race conditions and duplicate API calls
    }

    fun onBottomNavigationItemSelected(itemId: Int) {
        view?.onBottomNavigationItemSelected(itemId)
    }
}
