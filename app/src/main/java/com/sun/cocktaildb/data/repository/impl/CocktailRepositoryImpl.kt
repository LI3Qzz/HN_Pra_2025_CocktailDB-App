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

        try {
            TimeUnit.MILLISECONDS.sleep(300)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        
        return when (categoryId) {
            "1" -> { // Cocktails
                listOf(
                    Cocktail("1", "Mojito", "Refreshing mint cocktail", "", listOf("Rum", "Lime", "Mint"), "Mix all ingredients...", "Cocktails"),
                    Cocktail("2", "Margarita", "Classic tequila cocktail", "", listOf("Tequila", "Lime", "Triple Sec"), "Shake with ice...", "Cocktails"),
                    Cocktail("3", "Martini", "Elegant gin cocktail", "", listOf("Gin", "Vermouth"), "Stir with ice...", "Cocktails"),
                    Cocktail("4", "Cosmopolitan", "Pink cosmopolitan cocktail", "", listOf("Vodka", "Cranberry", "Lime"), "Shake with ice...", "Cocktails"),
                    Cocktail("5", "Old Fashioned", "Classic whiskey cocktail", "", listOf("Whiskey", "Sugar", "Bitters"), "Stir gently...", "Cocktails"),
                    Cocktail("6", "Daiquiri", "Cuban rum cocktail", "", listOf("Rum", "Lime", "Sugar"), "Shake with ice...", "Cocktails")
                )
            }
            "2" -> { // Beer
                listOf(
                    Cocktail("7", "Lager Beer", "Light and crisp beer", "", listOf("Malt", "Hops", "Water"), "Brewed traditionally", "Beer"),
                    Cocktail("8", "IPA", "India Pale Ale", "", listOf("Malt", "Hops", "Water"), "Hoppy and bitter", "Beer"),
                    Cocktail("9", "Stout", "Dark rich beer", "", listOf("Malt", "Hops", "Water"), "Creamy and smooth", "Beer"),
                    Cocktail("10", "Wheat Beer", "Light wheat beer", "", listOf("Wheat", "Hops", "Water"), "Smooth and citrusy", "Beer")
                )
            }
            "3" -> { // Wine
                listOf(
                    Cocktail("11", "Chardonnay", "White wine", "", listOf("Chardonnay grapes"), "Aged in oak barrels", "Wine"),
                    Cocktail("12", "Merlot", "Red wine", "", listOf("Merlot grapes"), "Smooth and fruity", "Wine"),
                    Cocktail("13", "Pinot Noir", "Light red wine", "", listOf("Pinot Noir grapes"), "Light and elegant", "Wine"),
                    Cocktail("14", "Sauvignon Blanc", "Crisp white wine", "", listOf("Sauvignon Blanc grapes"), "Fresh and zesty", "Wine")
                )
            }
            "4" -> { // Spirits
                listOf(
                    Cocktail("15", "Whiskey", "Premium whiskey", "", listOf("Grain", "Water"), "Aged in barrels", "Spirits"),
                    Cocktail("16", "Vodka", "Pure vodka", "", listOf("Grain", "Water"), "Distilled multiple times", "Spirits"),
                    Cocktail("17", "Rum", "Caribbean rum", "", listOf("Sugarcane", "Water"), "Aged in tropical climate", "Spirits"),
                    Cocktail("18", "Gin", "London dry gin", "", listOf("Juniper", "Botanicals"), "Distilled with botanicals", "Spirits")
                )
            }
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
