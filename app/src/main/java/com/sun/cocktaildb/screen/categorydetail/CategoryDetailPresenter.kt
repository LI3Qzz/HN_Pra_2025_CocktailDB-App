package com.sun.cocktaildb.screen.categorydetail

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.CocktailRepository
import com.sun.cocktaildb.utils.base.BasePresenter
import java.util.concurrent.Executors

class CategoryDetailPresenter(
    private val cocktailRepository: CocktailRepository,
    private val category: Category
) : BasePresenter<CategoryDetailView> {
    private var view: CategoryDetailView? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun setView(view: CategoryDetailView?) {
        this.view = view
    }

    override fun onStart() {
        view?.showCategory(category)
        loadCocktailsByCategory()
    }

    override fun onStop() {
        // Cleanup if needed
    }

    private fun loadCocktailsByCategory() {
        view?.showLoading()
        executor.execute {
            try {
                val cocktails = cocktailRepository.getCocktailsByCategory(category.id)
                mainHandler.post {
                    view?.showCocktails(cocktails)
                    view?.hideLoading()
                }
            } catch (e: Exception) {
                mainHandler.post {
                    view?.showError(e.message ?: "Error loading cocktails")
                    view?.hideLoading()
                }
            }
        }
    }

    fun onCocktailClicked(cocktail: Cocktail) {
        view?.onCocktailClicked(cocktail)
    }

    fun onBackClicked() {
        view?.navigateBack()
    }
}
