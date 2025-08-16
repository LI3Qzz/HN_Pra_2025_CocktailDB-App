package com.sun.cocktaildb.screen.home

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import com.sun.cocktaildb.utils.base.BasePresenter
import com.sun.cocktaildb.utils.FavoriteManager
import java.util.concurrent.Executors

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
                    view?.showCategories(categories)
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.showError(e.message ?: "Error loading categories")
                }
            }
        }
    }

    private fun loadPopularCocktails() {
        executor.execute {
            try {
                val cocktails = cocktailRepository.getPopularCocktails()
                // Update favorite status based on FavoriteManager
                val updatedCocktails = cocktails.map { cocktail ->
                    cocktail.copy(isFavorite = FavoriteManager.isFavorite(cocktail.id))
                }
                mainHandler.post {
                    view?.showPopularCocktails(updatedCocktails)
                    view?.hideLoading()
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.showError(e.message ?: "Error loading popular cocktails")
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
    
    fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        view?.onFavoriteClicked(cocktail, isFavorite)
    }

    fun onBottomNavigationItemSelected(itemId: Int) {
        view?.onBottomNavigationItemSelected(itemId)
    }
}
