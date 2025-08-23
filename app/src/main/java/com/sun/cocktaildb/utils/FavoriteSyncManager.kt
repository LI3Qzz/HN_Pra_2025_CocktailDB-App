package com.sun.cocktaildb.utils

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Manager to handle favorite synchronization across all screens
 * Ensures all screens (Home, Search, Favorite, Detail) stay in sync when favorites change
 */
object FavoriteSyncManager {
    private val repository = CocktailRepositoryImpl()
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    interface FavoriteUpdateListener {
        fun onFavoriteUpdated(cocktailId: String, isFavorite: Boolean)
        fun onFavoritesRefreshed()
    }

    private val listeners = mutableListOf<FavoriteUpdateListener>()

    fun registerListener(listener: FavoriteUpdateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun unregisterListener(listener: FavoriteUpdateListener) {
        listeners.remove(listener)
    }

    /**
     * Update favorite status and notify all screens
     * This is the main method called when user clicks favorite button
     */
    fun updateFavorite(cocktail: Cocktail, isFavorite: Boolean) {
        // Update local FavoriteManager immediately for UI responsiveness
        if (isFavorite) {
            FavoriteManager.addToFavorites(cocktail)
        } else {
            FavoriteManager.removeFromFavorites(cocktail)
        }
        
        // Immediately notify all screens about the specific cocktail update
        // This ensures instant UI updates across all screens
        notifyListeners(cocktail.id, isFavorite)
        
        // Save to Firebase in background
        executor.execute {
            try {
                if (isFavorite) {
                    repository.addFavourite(cocktail.id)
                } else {
                    repository.removeFavourite(cocktail.id)
                }
            } catch (e: Exception) {
                // Firebase operation failed, but local state is already updated
                // Could show a toast or handle error here if needed
            }
        }
    }

    fun refreshAllScreens() {
        executor.execute {
            try {
                repository.getFavouriteCocktails { result ->
                    if (result.isSuccess) {
                        val favoriteCocktails = result.getOrNull() ?: emptyList()
                        FavoriteManager.clearFavorites()
                        favoriteCocktails.forEach { cocktail ->
                            FavoriteManager.addToFavorites(cocktail)
                        }
                        mainHandler.post {
                            notifyAllScreensRefreshed()
                        }
                    } else {
                        mainHandler.post {
                            notifyAllScreensRefreshed() // Still notify screens to refresh from local data
                        }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    notifyAllScreensRefreshed() // Handle error silently and notify screens to refresh from local data
                }
            }
        }
    }

    fun isFavorite(cocktailId: String): Boolean {
        return FavoriteManager.isFavorite(cocktailId)
    }

    fun getFavoriteCocktails(): List<Cocktail> {
        return emptyList() // Placeholder, as FavoriteManager only stores IDs
    }

    fun getFavoriteCocktailIds(): Set<String> {
        return FavoriteManager.getFavoriteCocktails()
    }

    private fun notifyListeners(cocktailId: String, isFavorite: Boolean) {
        listeners.forEach { listener ->
            try {
                listener.onFavoriteUpdated(cocktailId, isFavorite)
            } catch (e: Exception) {
                listeners.remove(listener)
            }
        }
    }

    private fun notifyAllScreensRefreshed() {
        listeners.forEach { listener ->
            try {
                listener.onFavoritesRefreshed()
            } catch (e: Exception) {
                listeners.remove(listener)
            }
        }
    }
}
