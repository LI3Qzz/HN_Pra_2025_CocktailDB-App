package com.sun.cocktaildb.screen.cocktaildetail

import android.widget.Toast
import com.sun.cocktaildb.databinding.ActivityCocktailDetailBinding
import com.sun.cocktaildb.utils.base.BaseActivity

class CocktailDetailActivity : BaseActivity(), CocktailDetailView {

    private lateinit var binding: ActivityCocktailDetailBinding
    private lateinit var presenter: CocktailDetailPresenter

    override fun initView() {
        binding = ActivityCocktailDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = CocktailDetailPresenter()
        presenter.setView(this)
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
