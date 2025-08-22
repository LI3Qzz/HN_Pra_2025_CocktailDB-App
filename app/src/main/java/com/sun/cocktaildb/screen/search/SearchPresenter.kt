package com.sun.cocktaildb.screen.search

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.remote.CocktailRepository
import com.sun.cocktaildb.utils.base.BasePresenter
import java.util.concurrent.Executors

class SearchPresenter(
    private val cocktailRepository: CocktailRepository,
) : BasePresenter<SearchView> {
    private var view: SearchView? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    // Search state tracking
    private var currentSearchType = SearchType.NAME
    private var currentQuery = ""

    // Filter state
    private var selectedAlcoholicFilter: String? = null
    private var selectedIngredientFilter: String? = null

    // Cache for performance (LRU with max size)
    private val CACHE_MAX_SIZE = 100
    private val cachedResults =
        object : LinkedHashMap<String, List<Cocktail>>(CACHE_MAX_SIZE, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<Cocktail>>?): Boolean = size > CACHE_MAX_SIZE
        }

    // Search history
    private val searchHistory = mutableListOf<String>()
    private val maxHistorySize = 10

    override fun setView(view: SearchView?) {
        this.view = view
    }

    override fun onStart() {
        // Show search history when starting
        view?.showHistory(searchHistory)
    }

    override fun onStop() {
        // Clean up resources
    }

    // Search cocktails with query and search type
    fun searchCocktails(
        query: String,
        searchType: SearchType,
    ) {
        currentSearchType = searchType
        currentQuery = query.trim()

        val hasQuery = query.trim().isNotEmpty()
        val hasFilters = selectedAlcoholicFilter != null || selectedIngredientFilter != null
		
        if (!hasQuery && !hasFilters) {
            view?.clearSearchResults()
            view?.showHistory(searchHistory)
            return
        }

        // Check cache first
        val cacheKey = generateCacheKey(query.trim(), searchType, selectedAlcoholicFilter, selectedIngredientFilter)
        cachedResults[cacheKey]?.let { cached ->
            view?.hideHistory()
            view?.hideLoading()
            if (cached.isNotEmpty()) {
                view?.showSearchResults(cached)
            } else {
                view?.showNoResults()
            }
            return
        }

        // Add to search history for meaningful searches
        if (query.trim().length >= 2) {
            addToHistory(query.trim())
        }

        view?.hideHistory()
        view?.showLoading()

        executor.execute {
            try {
                var results =
                    when (searchType) {
                        SearchType.NAME -> {
                            if (hasQuery) {
                                val nameResults = cocktailRepository.searchCocktailsByName(query.trim())
                                if (nameResults.isEmpty() && query.trim().length == 1) {
                                    cocktailRepository.searchCocktailsByFirstLetter(query.trim())
                                } else {
                                    nameResults
                                }
                            } else {
                                cocktailRepository.searchCocktailsByFirstLetter("M")
                            }
                        }
                        SearchType.INGREDIENT -> {
                            if (hasQuery) {
                                cocktailRepository.searchCocktailsByIngredient(query.trim())
                            } else if (selectedIngredientFilter != null) {
                                cocktailRepository.searchCocktailsByIngredient(selectedIngredientFilter!!)
                            } else {
                                cocktailRepository.searchCocktailsByFirstLetter("M")
                            }
                        }
                        SearchType.FIRST_LETTER -> {
                            if (hasQuery) {
                                cocktailRepository.searchCocktailsByFirstLetter(query.trim())
                            } else {
                                cocktailRepository.searchCocktailsByFirstLetter("M")
                            }
                        }
                    }
				
                // Apply filters if selected
                results = applyFilters(results)

                
                // Load favorite status from Firebase and update results
                loadFavoriteStatusAndUpdateResults(results)
            } catch (e: Exception) {
                mainHandler.post {
                    view?.hideLoading()
                    view?.showError("Search failed: ${e.message}")
                }
            }
        }
    }

    private fun loadFavoriteStatusAndUpdateResults(results: List<Cocktail>) {
        // Get favorite IDs from Firebase
        cocktailRepository.getFavouriteCocktails { result ->
            if (result.isSuccess) {
                val favoriteIds = result.getOrNull()?.map { it.id } ?: emptyList()
                
                // Update cocktails with favorite status from Firebase
                val updatedResults = results.map { cocktail ->
                    val isFavorite = favoriteIds.contains(cocktail.id)
                    cocktail.copy(isFavorite = isFavorite)
                }
                
                // Update local FavoriteManager for consistency
                updatedResults.forEach { cocktail ->
                    if (cocktail.isFavorite) {
                        FavoriteManager.addToFavorites(cocktail)
                    } else {
                        FavoriteManager.removeFromFavorites(cocktail)
                    }
                }
                

				
                // Cache the results
                cachedResults[cacheKey] = results
				

                mainHandler.post {
                    view?.hideLoading()
                    if (results.isNotEmpty()) {
                        view?.showSearchResults(results)
                    } else {
                        view?.showNoResults()
                    }
                }
            } else {
                // Fallback to local FavoriteManager if Firebase fails
                val updatedResults = results.map { cocktail ->
                    cocktail.copy(isFavorite = FavoriteManager.isFavorite(cocktail.id))
                }
                
                mainHandler.post {
                    view?.hideLoading()
                    if (updatedResults.isNotEmpty()) {
                        view?.showSearchResults(updatedResults)
                    } else {
                        view?.showNoResults()
                    }
                }
            }
        }
    }

    // Set search type
    fun setSearchType(searchType: SearchType) {
        currentSearchType = searchType
    }

    // Get current search type
    fun getCurrentSearchType(): SearchType = currentSearchType

    // Clear search results and show history
    fun clearSearchResults() {
        currentQuery = ""
        selectedAlcoholicFilter = null
        selectedIngredientFilter = null
        view?.clearSearchResults()
        view?.showHistory(searchHistory)
    }

    // Set alcoholic filter
    fun setAlcoholicFilter(filter: String?) {
        selectedAlcoholicFilter = filter
        selectedIngredientFilter = null
        searchCocktails(currentQuery, currentSearchType)
    }

    // Set ingredient filter
    fun setIngredientFilter(filter: String?) {
        selectedIngredientFilter = filter
        selectedAlcoholicFilter = null
        searchCocktails(currentQuery, currentSearchType)
    }

    // Add query to search history
    private fun addToHistory(query: String) {
        searchHistory.remove(query)
        searchHistory.add(0, query)
        if (searchHistory.size > maxHistorySize) {
            searchHistory.removeAt(searchHistory.size - 1)
        }
    }

    // Remove query from search history
    fun removeFromHistory(query: String) {
        searchHistory.remove(query)
        view?.showHistory(searchHistory)
    }

    // Get search history
    fun getSearchHistory(): List<String> = searchHistory.toList()

    // Generate cache key for results
    private fun generateCacheKey(
        query: String,
        searchType: SearchType,
        alcoholicFilter: String?,
        ingredientFilter: String?,
    ): String = "${query}_${searchType}_${alcoholicFilter ?: "null"}_${ingredientFilter ?: "null"}"



    // Clear cache when needed
    fun clearCache() {
        cachedResults.clear()
    }


    // Apply filters to cocktail list
    private fun applyFilters(cocktails: List<Cocktail>): List<Cocktail> {
        var filteredCocktails = cocktails
		
        // Apply alcoholic filter
        selectedAlcoholicFilter?.let { alcoholicFilter ->
            filteredCocktails =
                filteredCocktails.filter { cocktail ->
                    cocktail.description.contains(alcoholicFilter, ignoreCase = true)
                }
        }
		
        // Apply ingredient filter
        selectedIngredientFilter?.let { ingredientFilter ->
            filteredCocktails =
                filteredCocktails.filter { cocktail ->
                    cocktail.ingredients.any { ingredient ->
                        ingredient.contains(ingredientFilter, ignoreCase = true)
                    }
                }
        }
		
        return filteredCocktails
    }
}

enum class SearchType {
    NAME,
    INGREDIENT,
    FIRST_LETTER,
}
