package com.sun.cocktaildb.screen.search

import android.R
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl

/**
 * Manages search functionality logic, separated from UI
 */
class SearchFragmentManager(
    private val context: Context,
    private val onSearchResults: (List<Cocktail>) -> Unit,
    private val onNoResults: () -> Unit,
    private val onError: (String) -> Unit
) {
    
    private lateinit var searchPresenter: SearchPresenter
    
    fun initialize() {
        if (!::searchPresenter.isInitialized) {
            searchPresenter = SearchPresenter(CocktailRepositoryImpl())
            searchPresenter.setView(object : SearchView {
                override fun showSearchResults(cocktails: List<Cocktail>) {
                    onSearchResults(cocktails)
                }
                
                override fun showNoResults() {
                    onNoResults()
                }
                
                override fun showLoading() {
                    // Loading will be handled by fragment
                }
                
                override fun hideLoading() {
                    // Loading will be handled by fragment
                }
                
                override fun clearSearchResults() {
                    onNoResults()
                }
                
                override fun onCocktailClicked(cocktail: Cocktail) {
                    // Click will be handled by fragment
                }
                
                override fun showError(message: String) {
                    onError(message)
                }
                
                override fun showHistory(historyItems: List<String>) {
                    // History will be handled by fragment
                }
                
                override fun hideHistory() {
                    // History will be handled by fragment
                }
                
                override fun removeFromHistory(query: String) {
                    // History removal will be handled by fragment
                }
                
                override fun showAlcoholicFilters(filters: List<String>) {
                    // Already handled in setupAlcoholicFilterDropdown
                }
                
                override fun showGlassTypes(glassTypes: List<String>) {
                    // TODO: Implement glass type filter
                }
                
                override fun showSearchTypeTabs() {
                    // Fragment already handles tabs
                }
                
                override fun updateSearchTypeTab(selectedType: SearchType) {
                    // Fragment already handles tabs
                }
                
                override fun showQuickFilters() {
                    // Fragment already handles filters
                }
                
                override fun showFilterOptions() {
                    // TODO: Implement advanced filter options
                }
            })
            searchPresenter.onStart()
        }
    }
    
    fun getSearchPresenter(): SearchPresenter {
        return searchPresenter
    }
    
    // Create adapter for alcoholic filter
    fun createAlcoholicFilterAdapter(): ArrayAdapter<String> {
        val alcoholicFilters = listOf("Select Type", "Alcoholic", "Non alcoholic", "Optional alcohol")
        return ArrayAdapter(context, R.layout.simple_spinner_item, alcoholicFilters).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
    }
    
    // Create adapter for ingredient filter
    fun createIngredientFilterAdapter(): ArrayAdapter<String> {
        val popularIngredients = listOf(
            "Select Ingredient", "Vodka", "Gin", "Rum", "Tequila", "Whiskey", "Brandy", "Triple sec",
            "Lime juice", "Lemon juice", "Orange juice", "Cranberry juice",
            "Sugar", "Grenadine", "Bitters", "Champagne", "Coffee", "Milk"
        )
        return ArrayAdapter(context, R.layout.simple_spinner_item, popularIngredients).apply {
            setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        }
    }
    
    // Listener for alcoholic filter
    fun createAlcoholicFilterListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (::searchPresenter.isInitialized && position > 0) {
                    val filters = listOf("Select Type", "Alcoholic", "Non alcoholic", "Optional alcohol")
                    searchPresenter.setAlcoholicFilter(filters[position])
                } else if (::searchPresenter.isInitialized) {
                    searchPresenter.setAlcoholicFilter(null)
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (::searchPresenter.isInitialized) {
                    searchPresenter.setAlcoholicFilter(null)
                }
            }
        }
    }
    
    // Listener for ingredient filter
    fun createIngredientFilterListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (::searchPresenter.isInitialized && position > 0) {
                    val ingredients = listOf(
                        "Select Ingredient", "Vodka", "Gin", "Rum", "Tequila", "Whiskey", "Brandy", "Triple sec",
                        "Lime juice", "Lemon juice", "Orange juice", "Cranberry juice",
                        "Sugar", "Grenadine", "Bitters", "Champagne", "Coffee", "Milk"
                    )
                    searchPresenter.setIngredientFilter(ingredients[position])
                } else if (::searchPresenter.isInitialized) {
                    searchPresenter.setIngredientFilter(null)
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (::searchPresenter.isInitialized) {
                    searchPresenter.setIngredientFilter(null)
                }
            }
        }
    }
    
    // Search cocktails
    fun searchCocktails(query: String, searchType: SearchType) {
        if (::searchPresenter.isInitialized) {
            searchPresenter.searchCocktails(query, searchType)
        }
    }
    
    // Clear search results
    fun clearSearchResults() {
        if (::searchPresenter.isInitialized) {
            searchPresenter.clearSearchResults()
        }
    }
    
    // Set search type
    fun setSearchType(searchType: SearchType) {
        if (::searchPresenter.isInitialized) {
            searchPresenter.setSearchType(searchType)
        }
    }
    
    // Get current search type
    fun getCurrentSearchType(): SearchType {
        return if (::searchPresenter.isInitialized) {
            searchPresenter.getCurrentSearchType()
        } else {
            SearchType.NAME
        }
    }
    
    // Remove query from search history
    fun removeFromHistory(query: String) {
        if (::searchPresenter.isInitialized) {
            searchPresenter.removeFromHistory(query)
        }
    }
}
