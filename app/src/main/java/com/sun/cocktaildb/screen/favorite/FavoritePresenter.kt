package com.sun.cocktaildb.screen.favorite

import com.sun.cocktaildb.data.repository.CocktailRepository
import com.sun.cocktaildb.utils.base.BasePresenter

class FavoritePresenter(
	private val repository: CocktailRepository
) : BasePresenter<FavoriteView> {

	private var view: FavoriteView? = null

	override fun setView(view: FavoriteView?) {
		this.view = view
	}

	override fun onStart() {
		loadFavorites()
	}

	override fun onStop() {
		// no-op
	}

	private fun loadFavorites() {
		val items = repository.getFavoriteCocktails()
		if (items.isEmpty()) {
			view?.showEmpty()
		} else {
			view?.showFavorites(items)
		}
	}
}
