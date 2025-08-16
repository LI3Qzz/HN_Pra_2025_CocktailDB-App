package com.sun.cocktaildb.screen.categorydetail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.sun.cocktaildb.data.model.Category
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.ActivityCategoryDetailBinding
import com.sun.cocktaildb.screen.home.adapter.PopularCocktailAdapter
import com.sun.cocktaildb.utils.base.BaseActivity

class CategoryDetailActivity : BaseActivity(), CategoryDetailView {
    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var presenter: CategoryDetailPresenter
    private lateinit var cocktailAdapter: PopularCocktailAdapter
    
    companion object {
        private const val EXTRA_CATEGORY = "extra_category"
        
        fun newIntent(context: Context, category: Category): Intent {
            return Intent(context, CategoryDetailActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY, category)
            }
        }
    }

    override fun initView() {
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CATEGORY, Category::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Category>(EXTRA_CATEGORY)
        } ?: throw IllegalArgumentException("Category is required")

        setupPresenter(category)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupPresenter(category: Category) {
        presenter = CategoryDetailPresenter(CocktailRepositoryImpl(), category)
        presenter.setView(this)
    }

    private fun setupRecyclerView() {
        cocktailAdapter = PopularCocktailAdapter { cocktail ->
            presenter.onCocktailClicked(cocktail)
        }
        binding.rvCocktails.apply {
            layoutManager = GridLayoutManager(this@CategoryDetailActivity, 2)
            adapter = cocktailAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onStart()
    }

    override fun onPause() {
        super.onPause()
        presenter.onStop()
    }

    // CategoryDetailView implementations
    override fun showCategory(category: Category) {
        binding.tvCategoryTitle.text = category.name
    }

    override fun showCocktails(cocktails: List<Cocktail>) {
        cocktailAdapter.updateCocktails(cocktails)
    }

    override fun showLoading() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = android.view.View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCocktailClicked(cocktail: Cocktail) {
        Toast.makeText(this, "Cocktail clicked: ${cocktail.name}", Toast.LENGTH_SHORT).show()
        // Navigate to cocktail detail
    }

    override fun navigateBack() {
        finish()
    }
}
