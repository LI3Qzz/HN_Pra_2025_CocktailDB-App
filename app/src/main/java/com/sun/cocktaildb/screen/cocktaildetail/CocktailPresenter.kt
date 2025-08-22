package com.sun.cocktaildb.screen.cocktaildetail

import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.remote.CocktailRepository
import com.sun.cocktaildb.utils.FavoriteManager
import com.sun.cocktaildb.utils.FavoriteSyncManager
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
                        // Check if cocktail is in favorites from Firebase
                        checkFavoriteStatus(cocktail.id)
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

    private fun checkFavoriteStatus(cocktailId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getFavouriteCocktails { result ->
                    if (result.isSuccess) {
                        val favoriteIds = result.getOrNull()?.map { it.id } ?: emptyList()
                        val isFavorite = favoriteIds.contains(cocktailId)
                        
                        // Update local FavoriteManager
                        if (isFavorite && currentCocktail != null) {
                            FavoriteManager.addToFavorites(currentCocktail!!)
                        }
                        
                        CoroutineScope(Dispatchers.Main).launch {
                            view?.updateFavoriteButton(isFavorite)
                        }
                    } else {
                        // Fallback to local FavoriteManager
                        val isFavorite = FavoriteSyncManager.isFavorite(cocktailId)
                        CoroutineScope(Dispatchers.Main).launch {
                            view?.updateFavoriteButton(isFavorite)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local FavoriteManager
                val isFavorite = FavoriteSyncManager.isFavorite(cocktailId)
                CoroutineScope(Dispatchers.Main).launch {
                    view?.updateFavoriteButton(isFavorite)
                }
            }
        }
    }

    override fun toggleFavorite(cocktail: Cocktail) {
        val isFavorite = FavoriteSyncManager.isFavorite(cocktail.id)
        val newFavoriteStatus = !isFavorite

        // Use FavoriteSyncManager to handle all favorite operations
        // This will automatically update Firebase and notify all screens
        FavoriteSyncManager.updateFavorite(cocktail, newFavoriteStatus)
        
        // Update UI immediately for better user experience
        view?.updateFavoriteButton(newFavoriteStatus)
    }

    override fun shareCocktail(cocktail: Cocktail) {
        view?.showShareDialog(cocktail)
    }
}
