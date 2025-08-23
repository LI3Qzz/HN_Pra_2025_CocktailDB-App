package com.sun.cocktaildb.screen.cocktaildetail

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.remote.CocktailRepository
import com.sun.cocktaildb.utils.FavoriteManager
import com.sun.cocktaildb.utils.FavoriteSyncManager
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CocktailPresenter(
    private val repository: CocktailRepository,
) : CocktailContract.Presenter {
    private var view: CocktailContract.View? = null
    private var currentCocktail: Cocktail? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun setView(view: CocktailContract.View?) {
        this.view = view
    }

    override fun onStart() {
        // No-op for now
    }

    override fun onStop() {
        // No-op for now
    }

    fun onDestroy() {
        view = null
    }

    override fun loadCocktailDetail(cocktailId: String) {
        view?.showLoading()
        executor.execute {
            try {
                val cocktail = repository.getCocktailById(cocktailId)
                mainHandler.post {
                    view?.hideLoading()
                    if (cocktail != null) {
                        currentCocktail = cocktail
                        view?.showCocktailDetail(cocktail)
                        checkFavoriteStatus(cocktailId)
                    } else {
                        view?.showError("Cocktail not found")
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.hideLoading()
                    view?.showError("Failed to load cocktail: ${e.message}")
                }
            }
        }
    }

    private fun checkFavoriteStatus(cocktailId: String) {
        // Use FavoriteSyncManager for immediate status check
        val isFavorite = FavoriteSyncManager.isFavorite(cocktailId)
        mainHandler.post {
            view?.updateFavoriteButton(isFavorite)
        }
        
        // Also sync with Firebase in background
        executor.execute {
            try {
                repository.getFavouriteCocktails { result ->
                    if (result.isSuccess) {
                        val favoriteIds = result.getOrNull()?.map { it.id } ?: emptyList()
                        val firebaseFavorite = favoriteIds.contains(cocktailId)
                        
                        // Update local manager if there's a difference
                        if (firebaseFavorite != isFavorite && currentCocktail != null) {
                            if (firebaseFavorite) {
                                FavoriteManager.addToFavorites(currentCocktail!!)
                            } else {
                                FavoriteManager.removeFromFavorites(currentCocktail!!)
                            }
                            mainHandler.post {
                                view?.updateFavoriteButton(firebaseFavorite)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Firebase check failed, but we already have local status
            }
        }
    }

    override fun toggleFavorite(cocktail: Cocktail) {
        val isFavorite = FavoriteSyncManager.isFavorite(cocktail.id)
        val newFavoriteStatus = !isFavorite
        FavoriteSyncManager.updateFavorite(cocktail, newFavoriteStatus)
        view?.updateFavoriteButton(newFavoriteStatus)
    }

    override fun shareCocktail(cocktail: Cocktail) {
        view?.showShareDialog(cocktail)
    }
}
