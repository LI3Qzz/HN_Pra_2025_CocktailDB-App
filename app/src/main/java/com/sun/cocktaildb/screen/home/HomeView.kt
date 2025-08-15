package com.sun.cocktaildb.screen.home

import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.utils.base.BaseView

interface HomeView : BaseView {
    fun showCategories(categories: List<Category>)

    fun showPopularCocktails(cocktails: List<Cocktail>)

    fun onCategoryClicked(category: Category)

    fun onCocktailClicked(cocktail: Cocktail)

    fun onBottomNavigationItemSelected(itemId: Int)
}
