package com.sun.cocktaildb.screen.cocktaildetail

import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import com.sun.cocktaildb.utils.FavoriteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CocktailPresenter(
    private val repository: CocktailRepository,
) : CocktailContract.Presenter {
    private var view: CocktailContract.View? = null
    private var currentCocktail: Cocktail? = null

    override fun onStart() {
        // Presenter started
    }

    override fun onStop() {
        // Presenter stopped
    }

    override fun setView(view: CocktailContract.View?) {
        this.view = view
    }

    fun onDestroy() {
        view = null
    }

    override fun loadCocktailDetail(cocktailId: String) {
        view?.showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cocktail = repository.getCocktailById(cocktailId)

                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    if (cocktail != null) {
                        currentCocktail = cocktail
                        // Check if cocktail is in favorites
                        val isFavorite = FavoriteManager.isFavorite(cocktail.id)
                        view?.updateFavoriteButton(isFavorite)
                        view?.showCocktailDetail(cocktail)
                    } else {
                        view?.showError("Failed to load cocktail details")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error: ${e.message}")
                }
            }
        }
    }

    override fun toggleFavorite(cocktail: Cocktail) {
        val isFavorite = FavoriteManager.isFavorite(cocktail.id)

        if (isFavorite) {
            FavoriteManager.removeFromFavorites(cocktail)
            view?.updateFavoriteButton(false)
        } else {
            FavoriteManager.addToFavorites(cocktail)
            view?.updateFavoriteButton(true)
        }
    }

    override fun shareCocktail(cocktail: Cocktail) {
        view?.showShareDialog(cocktail)
    }
}
