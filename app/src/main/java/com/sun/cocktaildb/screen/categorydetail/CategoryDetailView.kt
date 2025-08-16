package com.sun.cocktaildb.screen.categorydetail

import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.utils.base.BaseView

interface CategoryDetailView : BaseView {
    fun showCategory(category: Category)
    
    fun showCocktails(cocktails: List<Cocktail>)
    
    fun onCocktailClicked(cocktail: Cocktail)
    
    fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean)
    
    fun navigateBack()
}
