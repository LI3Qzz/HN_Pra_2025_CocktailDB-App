package com.sun.cocktaildb.screen.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sun.cocktaildb.R
import com.sun.cocktaildb.databinding.FragmentSearchBinding
import com.sun.cocktaildb.screen.cocktaildetail.CocktailActivity
import com.sun.cocktaildb.screen.search.adapter.HistoryAdapter
import com.sun.cocktaildb.screen.search.adapter.SearchAdapter
import com.sun.cocktaildb.utils.base.BaseFragment
import com.sun.cocktaildb.utils.dialog.LoadingDialog
import com.sun.cocktaildb.utils.FavoriteManager
import com.sun.cocktaildb.data.model.Cocktail

class SearchFragment : BaseFragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchFragmentManager: SearchFragmentManager
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: HistoryAdapter
    
    private val loadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        setupSearchAdapter()
        setupHistoryAdapter()
        setupSearchFragmentManager()
        setupSearchTabs()
        setupQuickFilters()
        setupSearchButton()
    }

    private fun setupSearchAdapter() {
        searchAdapter =
            SearchAdapter(
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
            // Ensure proper scrolling
            setHasFixedSize(false)
        }
    }

    private fun setupHistoryAdapter() {
        historyAdapter =
            HistoryAdapter(
                onHistoryItemClick = { query ->
                    binding.etSearch.setText(query)
                    searchFragmentManager.searchCocktails(query, searchFragmentManager.getCurrentSearchType())
                },
                onHistoryItemRemove = { query ->
                    searchFragmentManager.removeFromHistory(query)
                    // Refresh history display
                    showSearchHistory()
                },
            )

        binding.rvSearchHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupSearchFragmentManager() {
        searchFragmentManager =
            SearchFragmentManager(
                context = requireContext(),
                onSearchStarted = {
                    loadingDialog.show()
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    binding.llNoResults.visibility = View.GONE
                },
                onSearchResults = { cocktails ->
                    loadingDialog.hide()
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.llNoResults.visibility = View.GONE
                    val currentQuery = binding.etSearch.text.toString().trim()
                    searchAdapter.updateCocktails(cocktails, currentQuery)
                    // Force layout update
                    binding.rvSearchResults.requestLayout()
                    Toast.makeText(context, getString(R.string.found_n_cocktails, cocktails.size), Toast.LENGTH_SHORT).show()
                },
                onNoResults = {
                    loadingDialog.hide()
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    binding.llNoResults.visibility = View.VISIBLE
                    searchAdapter.updateCocktails(emptyList())
                },
                onError = { message ->
                    loadingDialog.hide()
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    binding.llNoResults.visibility = View.VISIBLE
                },
            )
        searchFragmentManager.initialize()

        // Show initial history
        showSearchHistory()
    }

    private fun showSearchHistory() {
        val history = searchFragmentManager.getSearchPresenter().getSearchHistory()
        if (history.isNotEmpty()) {
            binding.rvSearchHistory.visibility = View.VISIBLE
            binding.rvSearchResults.visibility = View.GONE
            binding.llNoResults.visibility = View.GONE
            historyAdapter.updateHistory(history)
        } else {
            binding.rvSearchHistory.visibility = View.GONE
        }
    }

    private fun hideSearchHistory() {
        binding.rvSearchHistory.visibility = View.GONE
    }

    private fun setupSearchTabs() {
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            searchFragmentManager.setSearchType(SearchType.NAME)
                            binding.etSearch.hint = getString(R.string.search_hint_name)
                            showAlcoholicFilterOnly()
                            searchFragmentManager.clearSearchResults()
                            showSearchHistory()
                        }
                        1 -> {
                            searchFragmentManager.setSearchType(SearchType.INGREDIENT)
                            binding.etSearch.hint = getString(R.string.search_hint_ingredient)
                            showIngredientFilterOnly()
                            searchFragmentManager.clearSearchResults()
                            showSearchHistory()
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
        binding.alcoholicFilterSpinner.adapter = searchFragmentManager.createAlcoholicFilterAdapter()
        binding.alcoholicFilterSpinner.onItemSelectedListener = searchFragmentManager.createAlcoholicFilterListener()
    }

    private fun setupIngredientFilterDropdown() {
        binding.ingredientFilterSpinner.adapter = searchFragmentManager.createIngredientFilterAdapter()
        binding.ingredientFilterSpinner.onItemSelectedListener = searchFragmentManager.createIngredientFilterListener()
    }

    private fun showAlcoholicFilterOnly() {
        binding.alcoholicFilterSpinner.visibility = View.VISIBLE
        binding.ingredientFilterSpinner.visibility = View.GONE

        val alcoholicLabel = binding.alcoholicFilterSpinner.parent as? LinearLayout
        alcoholicLabel?.visibility = View.VISIBLE

        val ingredientLabel = binding.ingredientFilterSpinner.parent as? LinearLayout
        ingredientLabel?.visibility = View.GONE
    }

    private fun showIngredientFilterOnly() {
        binding.alcoholicFilterSpinner.visibility = View.GONE
        binding.ingredientFilterSpinner.visibility = View.VISIBLE

        val alcoholicLabel = binding.alcoholicFilterSpinner.parent as? LinearLayout
        alcoholicLabel?.visibility = View.GONE

        val ingredientLabel = binding.ingredientFilterSpinner.parent as? LinearLayout
        ingredientLabel?.visibility = View.VISIBLE
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                // Add to search history
                searchFragmentManager.addToHistory(query)
                // Perform search
                searchFragmentManager.searchCocktails(query, searchFragmentManager.getCurrentSearchType())
            } else {
                Toast.makeText(context, getString(R.string.enter_search_query), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Show history when returning to fragment
        showSearchHistory()
    }

    private fun onFavoriteClicked(cocktail: Cocktail, isFavorite: Boolean) {
        if (isFavorite) {
            Toast.makeText(context, getString(R.string.added_to_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, getString(R.string.removed_from_favorites, cocktail.name), Toast.LENGTH_SHORT).show()
        }

        // Update the cocktail favorite status in the adapter
        searchAdapter.updateCocktailFavoriteStatus(cocktail.id, isFavorite)

        // Update favorite status in FavoriteManager
        if (isFavorite) {
            FavoriteManager.addToFavorites(cocktail)
        } else {
            FavoriteManager.removeFromFavorites(cocktail)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
