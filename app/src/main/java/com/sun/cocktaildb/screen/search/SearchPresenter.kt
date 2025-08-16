package com.sun.cocktaildb.screen.search

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import java.util.concurrent.Executors

class SearchPresenter(
    private val cocktailRepository: CocktailRepository
) : com.sun.cocktaildb.utils.base.BasePresenter<SearchView> {
    
    private var view: SearchView? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var searchJob: Runnable? = null
    
    // History management
    private val searchHistory = mutableListOf<String>()
    private val maxHistorySize = 10

    override fun setView(view: SearchView?) {
        this.view = view
    }

    override fun onStart() {
        // Load and show history
        view?.showHistory(searchHistory)
    }

    override fun onStop() {
        // Cancel any ongoing search
        searchJob?.let { mainHandler.removeCallbacks(it) }
    }

    fun searchCocktails(query: String) {
        if (query.trim().isEmpty()) {
            view?.clearSearchResults()
            view?.showHistory(searchHistory) // Show history when search is empty
            return
        }

        // Add to history first
        addToHistory(query.trim())

        // Hide history immediately when search starts
        view?.hideHistory()
        view?.showLoading()
        
        // Create new search job
        searchJob = Runnable {
            try {
                val results = cocktailRepository.searchCocktails(query.trim())
                mainHandler.post {
                    view?.hideLoading()
                    if (results.isNotEmpty()) {
                        view?.showSearchResults(results)
                    } else {
                        view?.showNoResults()
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.hideLoading()
                    view?.showError(e.message ?: "Search failed")
                }
            }
        }

        // Execute search immediately
        executor.execute(searchJob!!)
    }

    fun onCocktailClicked(cocktail: Cocktail) {
        view?.onCocktailClicked(cocktail)
    }

    fun removeFromHistory(historyItem: String) {
        searchHistory.remove(historyItem)
        // Only show history if we're in history mode (not showing search results)
        view?.showHistory(searchHistory)
    }
    
    private fun addToHistory(query: String) {
        // Remove if already exists (to move to top)
        searchHistory.remove(query)
        
        // Add to beginning of list
        searchHistory.add(0, query)
        
        // Keep only maxHistorySize items
        if (searchHistory.size > maxHistorySize) {
            searchHistory.removeAt(searchHistory.size - 1)
        }
        
        // Don't update view here - let the calling method decide when to show history
    }
}
