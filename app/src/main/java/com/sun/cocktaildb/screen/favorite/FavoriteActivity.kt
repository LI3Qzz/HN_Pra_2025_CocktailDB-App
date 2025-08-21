package com.sun.cocktaildb.screen.favorite

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.ActivityFavoriteBinding
import com.sun.cocktaildb.screen.home.HomeScreenActivity
import com.sun.cocktaildb.screen.search.SearchActivity
import com.sun.cocktaildb.utils.base.BaseActivity
import com.sun.cocktaildb.screen.favorite.adapter.FavoriteAdapter

class FavoriteActivity : BaseActivity(), FavoriteView {
	private lateinit var binding: ActivityFavoriteBinding
	private lateinit var presenter: FavoritePresenter
	private lateinit var adapter: FavoriteAdapter

	override fun initView() {
		binding = ActivityFavoriteBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setupPresenter()
		setupRecyclerView()
		setupBottomNavigation()
	}

	private fun setupPresenter() {
		presenter = FavoritePresenter(CocktailRepositoryImpl())
		presenter.setView(this)
	}

	private fun setupRecyclerView() {
		adapter = FavoriteAdapter(emptyList()) { cocktail ->
			Toast.makeText(this, getString(R.string.navigate_to_detail_screen, cocktail.name), Toast.LENGTH_SHORT).show()
		}
		binding.rvFavorites.layoutManager = LinearLayoutManager(this)
		binding.rvFavorites.adapter = adapter
	}

	private fun setupBottomNavigation() {
		binding.bottomNavigation.selectedItemId = R.id.navigation_favorites
		binding.bottomNavigation.setOnItemSelectedListener { item ->
			when (item.itemId) {
				R.id.navigation_home -> {
					startActivity(Intent(this, HomeScreenActivity::class.java))
					finish()
				}
				R.id.navigation_favorites -> Unit
				R.id.navigation_search -> startActivity(SearchActivity.newIntent(this))
				R.id.navigation_profile -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
			}
			true
		}
	}

	override fun onStart() {
		super.onStart()
		presenter.onStart()
	}

	override fun onStop() {
		super.onStop()
		presenter.onStop()
	}

	// BaseView
	override fun showLoading() { /* no-op */ }
	override fun hideLoading() { /* no-op */ }
	override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }

	// FavoriteView
	override fun showFavorites(cocktails: List<Cocktail>) {
		binding.emptyState.visibility = android.view.View.GONE
		binding.rvFavorites.visibility = android.view.View.VISIBLE
		adapter.submit(cocktails)
	}

	override fun showEmpty() {
		binding.rvFavorites.visibility = android.view.View.GONE
		binding.emptyState.visibility = android.view.View.VISIBLE
	}

	companion object {
		fun newIntent(context: Context): Intent = Intent(context, FavoriteActivity::class.java)
	}
}
