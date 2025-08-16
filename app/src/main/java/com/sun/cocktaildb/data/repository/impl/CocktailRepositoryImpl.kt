package com.sun.cocktaildb.data.repository.impl

import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class CocktailRepositoryImpl : CocktailRepository {
    private val baseUrl = "https://www.thecocktaildb.com/api/json/v1/1/"

    override fun getCategories(): List<Category> {
        try {
            val url = baseUrl + "list.php?c=list"
            val json = fetchJson(url)
            val drinksArray = JSONObject(json).optJSONArray("drinks") ?: JSONArray()
            val result = mutableListOf<Category>()
            for (i in 0 until drinksArray.length()) {
                val item = drinksArray.optJSONObject(i)
                val name = item?.optString("strCategory").orEmpty()
                if (name.isNotEmpty()) {
                    result.add(Category(
                        id = name,
                        name = name,
                        description = "Various $name types",
                        imageUrl = "https://example.com/$name.jpg"
                    ))
                }
            }
            return result
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            return listOf(
                Category("1", "Cocktails", "Classic cocktails", "https://example.com/cocktails.jpg"),
                Category("2", "Beer", "Various beer types", "https://example.com/beer.jpg"),
                Category("3", "Wine", "Red and white wines", "https://example.com/wine.jpg"),
                Category("4", "Spirits", "Premium spirits", "https://example.com/spirits.jpg"),
            )
        }
    }

    override fun getPopularCocktails(): List<Cocktail> {
        try {
            // Using Ordinary_Drink list as the "popular" source per requirement
            return getCocktailsByCategory("Ordinary_Drink")
        } catch (e: Exception) {
            // Fallback to mock data if API fails
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
    }

    override fun getCocktailsByCategory(categoryId: String): List<Cocktail> {
        try {
            // Treat categoryId as the category name since API uses names
            val encoded = categoryId.replace(' ', '_')
            val url = baseUrl + "filter.php?c=$encoded"
            val json = fetchJson(url)
            val drinksArray = JSONObject(json).optJSONArray("drinks") ?: JSONArray()
            val result = mutableListOf<Cocktail>()
            for (i in 0 until drinksArray.length()) {
                val item = drinksArray.optJSONObject(i)
                val id = item?.optString("idDrink").orEmpty()
                val name = item?.optString("strDrink").orEmpty()
                val thumb = item?.optString("strDrinkThumb").orEmpty()
                val category = item?.optString("strCategory").orEmpty()
                if (id.isNotEmpty() && name.isNotEmpty()) {
                    result.add(
                        Cocktail(
                            id = id,
                            name = name,
                            description = "Delicious ${category.ifEmpty { categoryId }}",
                            imageUrl = thumb.ifEmpty { "https://example.com/placeholder.jpg" },
                            ingredients = listOf("Ingredients not available"),
                            instructions = "Instructions not available",
                            category = category.ifEmpty { categoryId }
                        )
                    )
                }
            }
            return result
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            return when (categoryId) {
                "1", "Cocktails" -> {
                    listOf(
                        Cocktail("1", "Mojito", "Refreshing mint cocktail", "", listOf("Rum", "Lime", "Mint"), "Mix all ingredients...", "Cocktails"),
                        Cocktail("2", "Margarita", "Classic tequila cocktail", "", listOf("Tequila", "Lime", "Triple Sec"), "Shake with ice...", "Cocktails"),
                        Cocktail("3", "Martini", "Elegant gin cocktail", "", listOf("Gin", "Vermouth"), "Stir with ice...", "Cocktails")
                    )
                }
                "2", "Beer" -> {
                    listOf(
                        Cocktail("7", "Lager Beer", "Light and crisp beer", "", listOf("Malt", "Hops", "Water"), "Brewed traditionally", "Beer"),
                        Cocktail("8", "IPA", "India Pale Ale", "", listOf("Malt", "Hops", "Water"), "Hoppy and bitter", "Beer")
                    )
                }
                "3", "Wine" -> {
                    listOf(
                        Cocktail("11", "Chardonnay", "White wine", "", listOf("Chardonnay grapes"), "Aged in oak barrels", "Wine"),
                        Cocktail("12", "Merlot", "Red wine", "", listOf("Merlot grapes"), "Smooth and fruity", "Wine")
                    )
                }
                "4", "Spirits" -> {
                    listOf(
                        Cocktail("15", "Whiskey", "Premium whiskey", "", listOf("Grain", "Water"), "Aged in barrels", "Spirits"),
                        Cocktail("16", "Vodka", "Pure vodka", "", listOf("Grain", "Water"), "Distilled multiple times", "Spirits")
                    )
                }
                else -> emptyList()
            }
        }
    }

    override fun getCocktailById(id: String): Cocktail? {
        try {
            val url = baseUrl + "lookup.php?i=$id"
            val json = fetchJson(url)
            val drinksArray = JSONObject(json).optJSONArray("drinks") ?: return null
            val item = drinksArray.optJSONObject(0) ?: return null
            val name = item.optString("strDrink").orEmpty()
            val thumb = item.optString("strDrinkThumb").orEmpty()
            val instructions = item.optString("strInstructions").orEmpty()
            val category = item.optString("strCategory").orEmpty()
            
            if (name.isEmpty()) return null
            
            // Extract ingredients (strIngredient1 to strIngredient15)
            val ingredients = mutableListOf<String>()
            for (i in 1..15) {
                val ingredient = item.optString("strIngredient$i").orEmpty()
                if (ingredient.isNotEmpty()) {
                    ingredients.add(ingredient)
                }
            }
            
            return Cocktail(
                id = id,
                name = name,
                description = "Delicious ${category.ifEmpty { "Unknown" }}",
                imageUrl = thumb.ifEmpty { "https://example.com/placeholder.jpg" },
                ingredients = ingredients.ifEmpty { listOf("Ingredients not available") },
                instructions = instructions.ifEmpty { "Instructions not available" },
                category = category.ifEmpty { "Unknown" }
            )
        } catch (e: Exception) {
            return null
        }
    }

    override fun searchCocktails(query: String): List<Cocktail> {
        try {
            val url = baseUrl + "search.php?s=" + query.trim()
            val json = fetchJson(url)
            val drinksArray = JSONObject(json).optJSONArray("drinks") ?: JSONArray()
            val result = mutableListOf<Cocktail>()
            for (i in 0 until drinksArray.length()) {
                val item = drinksArray.optJSONObject(i)
                val id = item?.optString("idDrink").orEmpty()
                val name = item?.optString("strDrink").orEmpty()
                val thumb = item?.optString("strDrinkThumb").orEmpty()
                val category = item?.optString("strCategory").orEmpty()
                if (id.isNotEmpty() && name.isNotEmpty()) {
                    result.add(Cocktail(
                        id = id,
                        name = name,
                        description = "Search result for $query",
                        imageUrl = thumb.ifEmpty { "https://example.com/placeholder.jpg" },
                        ingredients = listOf("Ingredients not available"),
                        instructions = "Instructions not available",
                        category = category.ifEmpty { "Unknown" }
                    ))
                }
            }
            return result
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override fun toggleFavorite(cocktailId: String): Boolean {
        return true
    }

    override fun getFavoriteCocktails(): List<Cocktail> {
        return getPopularCocktails().take(2)
    }

    private fun fetchJson(urlString: String): String {
        val url = URL(urlString)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            doInput = true
        }
        try {
            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val reader = BufferedReader(InputStreamReader(stream))
            val builder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            reader.close()
            if (responseCode !in 200..299) {
                throw RuntimeException("HTTP $responseCode: ${builder.toString()}")
            }
            return builder.toString()
        } finally {
            connection.disconnect()
        }
    }
}
