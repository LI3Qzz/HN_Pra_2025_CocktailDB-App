package com.sun.cocktaildb.utils

import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manager to handle favorite synchronization across all screens
 * Ensures all screens (Home, Search, Favorite, Detail) stay in sync when favorites change
 */
object FavoriteSyncManager {
    private val repository = CocktailRepositoryImpl()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Callback interface for screen updates
    interface FavoriteUpdateListener {
        fun onFavoriteUpdated(cocktailId: String, isFavorite: Boolean)
        fun onFavoritesRefreshed()
    }
    
    private val listeners = mutableListOf<FavoriteUpdateListener>()
    
    /**
     * Register a screen to receive favorite updates
     */
    fun registerListener(listener: FavoriteUpdateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }
    
    /**
     * Unregister a screen from favorite updates
     */
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
        
        // Save to Firebase and notify all screens
        scope.launch {
            try {
                if (isFavorite) {
                    repository.addFavourite(cocktail.id)
                } else {
                    repository.removeFavourite(cocktail.id)
                }
                
                // Notify all registered screens about the specific cocktail update
                notifyListeners(cocktail.id, isFavorite)
            } catch (e: Exception) {
                // If Firebase fails, still notify local screens
                notifyListeners(cocktail.id, isFavorite)
            }
        }
    }
    
    /**
     * Refresh all screens with current favorite status from Firebase
     * This ensures all screens are in sync with the server
     */
    fun refreshAllScreens() {
        scope.launch {
            try {
                // Get current favorites from Firebase
                repository.getFavouriteCocktails { result ->
                    if (result.isSuccess) {
                        val favoriteCocktails = result.getOrNull() ?: emptyList()
                        
                        // Update FavoriteManager with current favorites
                        FavoriteManager.clearFavorites()
                        favoriteCocktails.forEach { cocktail ->
                            FavoriteManager.addToFavorites(cocktail)
                        }
                        
                        // Notify all screens to refresh
                        notifyAllScreensRefreshed()
                    } else {
                        // If Firebase fails, still notify screens to refresh from local data
                        notifyAllScreensRefreshed()
                    }
                }
            } catch (e: Exception) {
                // Handle error silently and notify screens to refresh from local data
                notifyAllScreensRefreshed()
            }
        }
    }
    
    /**
     * Get current favorite status for a cocktail
     */
    fun isFavorite(cocktailId: String): Boolean {
        return FavoriteManager.isFavorite(cocktailId)
    }
    
    /**
     * Get all favorite cocktails from local storage
     */
    fun getFavoriteCocktails(): List<Cocktail> {
        // This should return actual Cocktail objects, not just IDs
        // For now, return empty list since we only store IDs in FavoriteManager
        return emptyList()
    }
    
    /**
     * Get favorite cocktail IDs
     */
    fun getFavoriteCocktailIds(): Set<String> {
        return FavoriteManager.getFavoriteCocktails()
    }
    
    private fun notifyListeners(cocktailId: String, isFavorite: Boolean) {
        listeners.forEach { listener ->
            try {
                listener.onFavoriteUpdated(cocktailId, isFavorite)
            } catch (e: Exception) {
                // Remove broken listeners
                listeners.remove(listener)
            }
        }
    }
    
    private fun notifyAllScreensRefreshed() {
        listeners.forEach { listener ->
            try {
                listener.onFavoritesRefreshed()
            } catch (e: Exception) {
                // Remove broken listeners
                listeners.remove(listener)
            }
        }
    }
}
