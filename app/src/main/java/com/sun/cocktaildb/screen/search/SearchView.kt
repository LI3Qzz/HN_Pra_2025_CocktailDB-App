package com.sun.cocktaildb.screen.search

import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.utils.base.BaseView

interface SearchView : BaseView {
    fun showSearchResults(cocktails: List<Cocktail>)
    fun showNoResults()
    override fun showLoading()
    override fun hideLoading()
    fun clearSearchResults()
    fun onCocktailClicked(cocktail: Cocktail)
    fun showHistory(historyItems: List<String>)
    fun hideHistory()
    fun removeFromHistory(historyItem: String)
}
