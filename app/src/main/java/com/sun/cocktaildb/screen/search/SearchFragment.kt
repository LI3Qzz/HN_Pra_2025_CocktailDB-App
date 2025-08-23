package com.sun.cocktaildb.screen.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.data.repository.impl.CocktailRepositoryImpl
import com.sun.cocktaildb.databinding.FragmentSearchBinding
import com.sun.cocktaildb.screen.cocktaildetail.CocktailActivity
import com.sun.cocktaildb.screen.search.adapter.HistoryAdapter
import com.sun.cocktaildb.screen.search.adapter.SearchAdapter
import com.sun.cocktaildb.utils.FavoriteSyncManager
import com.sun.cocktaildb.utils.base.BaseFragment
import com.sun.cocktaildb.utils.dialog.LoadingDialog

class SearchFragment : BaseFragment(), SearchView, FavoriteSyncManager.FavoriteUpdateListener {
    private val binding: FragmentSearchBinding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }
    private lateinit var presenter: SearchPresenter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val loadingDialog by lazy {
        LoadingDialog(this@SearchFragment.requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun initView() {
        setupPresenter()
        setupRecyclerViews()
        setupSearchInput()
        setupSearchTabs()
        setupQuickFilters()
        setupSearchButton()
        FavoriteSyncManager.registerListener(this)
    }

    private fun setupPresenter() {
        presenter = SearchPresenter(CocktailRepositoryImpl())
        presenter.setView(this)
    }

    private fun setupRecyclerViews() {
        // Setup Search Results RecyclerView
        searchAdapter = SearchAdapter(
            onCocktailClicked = { cocktail ->
                val intent = CocktailActivity.newIntent(requireContext(), cocktail.id)
                startActivity(intent)
            },
            onFavoriteClickListener = { cocktail, isFavorite ->
                onFavoriteClicked(cocktail, isFavorite)
            }
        )
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
            setHasFixedSize(false)
        }

        // Setup History RecyclerView
        historyAdapter = HistoryAdapter(
            onHistoryItemClickListener = { query ->
                binding.etSearch.setText(query)
                presenter.searchCocktails(query, presenter.getCurrentSearchType())
            },
            onHistoryItemDeleteClickListener = { query ->
                presenter.removeFromHistory(query)
            }
        )
        binding.rvSearchHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                if (query.isNotEmpty()) {
                    presenter.searchCocktails(query, presenter.getCurrentSearchType())
                } else {
                    presenter.clearSearchResults()
                    showSearchHistory()
                }
            }
        })
    }

    private fun setupSearchTabs() {
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            presenter.setSearchType(SearchType.NAME)
                            binding.etSearch.hint = getString(R.string.search_hint_name)
                            showAlcoholicFilterOnly()
                            // Clear current results and load new ones for NAME type
                            binding.etSearch.setText("")
                            loadCocktailsForType(SearchType.NAME)
                        }
                        1 -> {
                            presenter.setSearchType(SearchType.INGREDIENT)
                            binding.etSearch.hint = getString(R.string.search_hint_ingredient)
                            showIngredientFilterOnly()
                            // Clear current results and load new ones for INGREDIENT type
                            binding.etSearch.setText("")
                            loadCocktailsForType(SearchType.INGREDIENT)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            },
        )
    }

    private fun setupQuickFilters() {
        setupAlcoholicFilterDropdown()
        setupIngredientFilterDropdown()
        showAlcoholicFilterOnly()
    }

    private fun setupAlcoholicFilterDropdown() {
        val alcoholicFilters = arrayOf("All", "Alcoholic", "Non_Alcoholic", "Optional_Alcohol")
        val alcoholicAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, alcoholicFilters)
        alcoholicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.alcoholicFilterSpinner.adapter = alcoholicAdapter

        binding.alcoholicFilterSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filter = when (position) {
                    0 -> null
                    1 -> "Alcoholic"
                    2 -> "Non_Alcoholic"
                    3 -> "Optional_Alcohol"
                    else -> null
                }
                presenter.setAlcoholicFilter(filter)
                // Trigger search with current query and new filter
                val currentQuery = binding.etSearch.text.toString().trim()
                presenter.searchCocktails(currentQuery, presenter.getCurrentSearchType())
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun setupIngredientFilterDropdown() {
        val ingredientFilters = arrayOf("All", "Gin", "Vodka", "Rum", "Tequila", "Whiskey", "Brandy", "Liqueur")
        val ingredientAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ingredientFilters)
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ingredientFilterSpinner.adapter = ingredientAdapter

        binding.ingredientFilterSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filter = when (position) {
                    0 -> null
                    else -> ingredientFilters[position]
                }
                presenter.setIngredientFilter(filter)
                // Trigger search with current query and new filter
                val currentQuery = binding.etSearch.text.toString().trim()
                presenter.searchCocktails(currentQuery, presenter.getCurrentSearchType())
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun showAlcoholicFilterOnly() {
        binding.alcoholicFilterSpinner.visibility = View.VISIBLE
        binding.ingredientFilterSpinner.visibility = View.GONE
    }

    private fun showIngredientFilterOnly() {
        binding.alcoholicFilterSpinner.visibility = View.GONE
        binding.ingredientFilterSpinner.visibility = View.VISIBLE
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                presenter.searchCocktails(query, presenter.getCurrentSearchType())
                presenter.addToHistory(query)
            } else {
                Toast.makeText(context, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Load cocktails based on current search type
        val currentType = presenter.getCurrentSearchType()
        loadCocktailsForType(currentType)
        refreshFavoriteStatusFromDetailScreen()
    }

    private fun refreshFavoriteStatusFromDetailScreen() {
        val currentCocktails = searchAdapter.getCurrentCocktails()
        if (currentCocktails.isNotEmpty()) {
            currentCocktails.forEach { cocktail ->
                val isFavorite = FavoriteSyncManager.isFavorite(cocktail.id)
                searchAdapter.updateCocktailFavoriteStatus(cocktail.id, isFavorite)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FavoriteSyncManager.unregisterListener(this)
    }

    // FavoriteSyncManager.FavoriteUpdateListener implementations
    override fun onFavoriteUpdated(cocktailId: String, isFavorite: Boolean) {
        searchAdapter.updateCocktailFavoriteStatus(cocktailId, isFavorite)
    }

    override fun onFavoritesRefreshed() {
        refreshSearchResultsFromOtherScreens()
    }

    private fun refreshSearchResultsFromOtherScreens() {
        val currentCocktails = searchAdapter.getCurrentCocktails()
        if (currentCocktails.isNotEmpty()) {
            currentCocktails.forEach { cocktail ->
                val isFavorite = FavoriteSyncManager.isFavorite(cocktail.id)
                searchAdapter.updateCocktailFavoriteStatus(cocktail.id, isFavorite)
            }
        }
    }

    // SearchView implementations
    override fun showSearchResults(cocktails: List<Cocktail>) {
        binding.rvSearchResults.visibility = View.VISIBLE
        binding.rvSearchHistory.visibility = View.GONE
        binding.llNoResults.visibility = View.GONE
        val currentQuery = binding.etSearch.text.toString().trim()
        searchAdapter.updateCocktails(cocktails, currentQuery)
    }

    override fun showNoResults() {
        binding.rvSearchResults.visibility = View.GONE
        binding.rvSearchHistory.visibility = View.GONE
        binding.llNoResults.visibility = View.VISIBLE
    }

    override fun showHistory(history: List<String>) {
        binding.rvSearchResults.visibility = View.GONE
        binding.rvSearchHistory.visibility = View.VISIBLE
        binding.llNoResults.visibility = View.GONE
        historyAdapter.updateHistory(history)
    }

    override fun hideHistory() {
        binding.rvSearchHistory.visibility = View.GONE
    }

    override fun clearSearchResults() {
        binding.rvSearchResults.visibility = View.GONE
        binding.llNoResults.visibility = View.GONE
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.hide()
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onCocktailClicked(cocktail: Cocktail) {
        val intent = CocktailActivity.newIntent(requireContext(), cocktail.id)
        startActivity(intent)
    }

    override fun removeFromHistory(historyItem: String) {
        presenter.removeFromHistory(historyItem)
    }

    private fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        if (isFavorite) {
            Toast.makeText(context, getString(R.string.added_to_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, getString(R.string.removed_from_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        }
        searchAdapter.updateCocktailFavoriteStatus(cocktail.id, isFavorite)
        FavoriteSyncManager.updateFavorite(cocktail, isFavorite)
    }

    override fun showAlcoholicFilters(filters: List<String>) {
        // Implementation for showing alcoholic filters
    }

    override fun showGlassTypes(glassTypes: List<String>) {
        // Implementation for showing glass types
    }

    override fun showSearchTypeTabs() {
        // Implementation for showing search type tabs
    }

    override fun updateSearchTypeTab(selectedType: SearchType) {
        // Implementation for updating search type tab
    }

    override fun showQuickFilters() {
        // Implementation for showing quick filters
    }

    override fun showFilterOptions() {
        // Implementation for showing filter options
    }

    private fun loadCocktailsForType(searchType: SearchType) {
        // Load cocktails based on the specific search type
        when (searchType) {
            SearchType.NAME -> {
                // For NAME type, load cocktails starting with "M"
                presenter.searchCocktails("", SearchType.NAME)
            }
            SearchType.INGREDIENT -> {
                // For INGREDIENT type, load popular cocktails
                presenter.searchCocktails("", SearchType.INGREDIENT)
            }
            SearchType.FIRST_LETTER -> {
                // For FIRST_LETTER type, load cocktails starting with "M"
                presenter.searchCocktails("", SearchType.FIRST_LETTER)
            }
        }
    }

    private fun loadDefaultCocktails() {
        // Load some default cocktails to show when search screen opens
        loadCocktailsForType(presenter.getCurrentSearchType())
    }

    private fun showSearchHistory() {
        val history = presenter.getSearchHistory()
        if (history.isNotEmpty()) {
            showHistory(history)
        }
    }
}
