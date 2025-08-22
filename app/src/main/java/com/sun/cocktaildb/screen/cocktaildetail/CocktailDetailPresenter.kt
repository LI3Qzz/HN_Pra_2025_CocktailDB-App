package com.sun.cocktaildb.screen.cocktaildetail

import com.sun.cocktaildb.utils.base.BasePresenter

class CocktailDetailPresenter : BasePresenter<CocktailDetailView> {
    private var view: CocktailDetailView? = null

    override fun setView(view: CocktailDetailView?) {
        this.view = view
    }

    override fun onStart() {
        // No-op for initial skeleton
    }

    override fun onStop() {
        // No-op for initial skeleton
    }
}
