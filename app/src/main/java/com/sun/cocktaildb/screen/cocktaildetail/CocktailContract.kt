package com.sun.cocktaildb.screen.cocktaildetail

import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.utils.base.BasePresenter
import com.sun.cocktaildb.utils.base.BaseView

interface CocktailContract {
    interface View : BaseView {
        fun showCocktailDetail(cocktail: Cocktail)

        override fun showLoading()

        override fun hideLoading()

        override fun showError(message: String)

        fun updateFavoriteButton(isFavorite: Boolean)

        fun showShareDialog(cocktail: Cocktail)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCocktailDetail(cocktailId: String)

        fun toggleFavorite(cocktail: Cocktail)

        fun shareCocktail(cocktail: Cocktail)
    }
}
