package com.sun.cocktaildb.data.repository.impl

import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import java.util.concurrent.TimeUnit

class CocktailRepositoryImpl : CocktailRepository {
    override fun getCategories(): List<Category> {
        try {
            TimeUnit.MILLISECONDS.sleep(300)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return listOf(
            Category("1", "Cocktails", "Classic cocktails", "https://example.com/cocktails.jpg"),
            Category("2", "Beer", "Various beer types", "https://example.com/beer.jpg"),
            Category("3", "Wine", "Red and white wines", "https://example.com/wine.jpg"),
            Category("4", "Spirits", "Premium spirits", "https://example.com/spirits.jpg"),
        )
    }

    override fun getPopularCocktails(): List<Cocktail> {
        try {
            TimeUnit.MILLISECONDS.sleep(300)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return listOf(
            Cocktail(
                "1",
                "Mojito",
                "Refreshing mint cocktail",
                "https://example.com/mojito.jpg",
                listOf("Rum", "Lime", "Mint", "Sugar", "Soda"),
                "Mix all ingredients...",
                "Cocktails",
            ),
            Cocktail(
                "2",
                "Margarita",
                "Classic tequila cocktail",
                "https://example.com/margarita.jpg",
                listOf("Tequila", "Lime", "Triple Sec"),
                "Shake with ice...",
                "Cocktails",
            ),
            Cocktail(
                "3",
                "Martini",
                "Elegant gin cocktail",
                "https://example.com/martini.jpg",
                listOf("Gin", "Vermouth"),
                "Stir with ice...",
                "Cocktails",
            ),
        )
    }

    override fun getCocktailsByCategory(categoryId: String): List<Cocktail> {
        return when (categoryId) {
            "1" -> getPopularCocktails()
            else -> emptyList()
        }
    }

    override fun getCocktailById(id: String): Cocktail? {
        return getPopularCocktails().find { it.id == id }
    }

    override fun searchCocktails(query: String): List<Cocktail> {
        return getPopularCocktails().filter {
            it.name.contains(query, ignoreCase = true) ||
                it.ingredients.any { ingredient -> ingredient.contains(query, ignoreCase = true) }
        }
    }

    override fun toggleFavorite(cocktailId: String): Boolean {
        return true
    }

    override fun getFavoriteCocktails(): List<Cocktail> {
        return getPopularCocktails().take(2)
    }
}
