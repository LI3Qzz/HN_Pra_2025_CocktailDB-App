package com.sun.cocktaildb.screen.favorite

import com.sun.cocktaildb.utils.base.BasePresenter

class FavoritePresenter : BasePresenter<FavoriteView> {
	private var view: FavoriteView? = null

	override fun setView(view: FavoriteView?) {
		this.view = view
	}

	override fun onStart() { /* no-op */ }

	override fun onStop() { /* no-op */ }
}