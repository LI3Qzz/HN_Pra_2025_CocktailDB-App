package com.sun.cocktaildb.data.repository

import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail

interface CocktailRepository {
    fun getCategories(): List<Category>

    fun getPopularCocktails(): List<Cocktail>

    fun getCocktailsByCategory(categoryId: String): List<Cocktail>

    fun getCocktailById(id: String): Cocktail?

    fun searchCocktails(query: String): List<Cocktail>

    fun toggleFavorite(cocktailId: String): Boolean

    fun getFavoriteCocktails(): List<Cocktail>
    
    // New search methods
    fun searchCocktailsByName(query: String): List<Cocktail>
    
    fun searchCocktailsByFirstLetter(letter: String): List<Cocktail>
    
    fun searchCocktailsByIngredient(ingredient: String): List<Cocktail>
    
    fun filterCocktailsByAlcoholic(isAlcoholic: Boolean): List<Cocktail>
    
    fun filterCocktailsByCategory(category: String): List<Cocktail>
    
    fun getAlcoholicFilters(): List<String>
    
    fun getGlassTypes(): List<String>
    
    fun getIngredientsList(): List<String>
}
