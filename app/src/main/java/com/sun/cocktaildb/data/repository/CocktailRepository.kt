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
}
