package com.sun.cocktaildb.utils

import com.sun.cocktaildb.data.model.Cocktail

object FavoriteManager {
    private val favoriteCocktails = mutableSetOf<String>() // Store cocktail IDs
    
    fun addToFavorites(cocktail: Cocktail) {
        favoriteCocktails.add(cocktail.id)
    }
    
    fun removeFromFavorites(cocktail: Cocktail) {
        favoriteCocktails.remove(cocktail.id)
    }
    
    fun isFavorite(cocktailId: String): Boolean {
        return favoriteCocktails.contains(cocktailId)
    }
    
    fun getFavoriteCocktails(): Set<String> {
        return favoriteCocktails.toSet()
    }
    
    fun clearFavorites() {
        favoriteCocktails.clear()
    }
}
