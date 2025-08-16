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

class CocktailRepositoryImpl : CocktailRepository {
    private val baseUrl = "https://www.thecocktaildb.com/api/json/v1/1/"

    override fun getCategories(): List<Category> {
        val url = baseUrl + "list.php?c=list"
        val json = fetchJson(url)
        val drinksArray = JSONObject(json).optJSONArray("drinks") ?: JSONArray()
        val result = mutableListOf<Category>()
        for (i in 0 until drinksArray.length()) {
            val item = drinksArray.optJSONObject(i)
            val name = item?.optString("strCategory").orEmpty()
            if (name.isNotEmpty()) {
                result.add(Category(name = name))
            }
        }
        return result
    }

    override fun getPopularCocktails(): List<Cocktail> {
        // Using Ordinary_Drink list as the "popular" source per requirement
        return getCocktailsByCategory("Ordinary_Drink")
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
            if (id.isNotEmpty() && name.isNotEmpty()) {
                result.add(
                    Cocktail(
                        id = id,
                        name = name,
                        imageUrl = thumb,
                    ),
                )
            }

        }
        return result
    }

    override fun getCocktailById(id: String): Cocktail? {
        // Optional: lookup details. Only id, name, imageUrl are mapped here
        val url = baseUrl + "lookup.php?i=$id"
        val json = fetchJson(url)
        val drinksArray = JSONObject(json).optJSONArray("drinks") ?: return null
        val item = drinksArray.optJSONObject(0) ?: return null
        val name = item.optString("strDrink").orEmpty()
        val thumb = item.optString("strDrinkThumb").orEmpty()
        if (name.isEmpty()) return null
        return Cocktail(
            id = id,
            name = name,
            imageUrl = thumb,
        )
    }

    override fun searchCocktails(query: String): List<Cocktail> {
        // Optional; not used in this task
        val url = baseUrl + "search.php?s=" + query.trim()
        val json = fetchJson(url)
        val drinksArray = JSONObject(json).optJSONArray("drinks") ?: JSONArray()
        val result = mutableListOf<Cocktail>()
        for (i in 0 until drinksArray.length()) {
            val item = drinksArray.optJSONObject(i)
            val id = item?.optString("idDrink").orEmpty()
            val name = item?.optString("strDrink").orEmpty()
            val thumb = item?.optString("strDrinkThumb").orEmpty()
            if (id.isNotEmpty() && name.isNotEmpty()) {
                result.add(Cocktail(id = id, name = name, imageUrl = thumb))
            }
        }
        return result
    }

    override fun toggleFavorite(cocktailId: String): Boolean {
        // Not implemented in this task
        return false
    }

    override fun getFavoriteCocktails(): List<Cocktail> {
        // Not implemented in this task
        return emptyList()
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
