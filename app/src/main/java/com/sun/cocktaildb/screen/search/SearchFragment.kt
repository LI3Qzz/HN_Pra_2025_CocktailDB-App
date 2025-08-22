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

class SearchFragment : BaseFragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchFragmentManager: SearchFragmentManager
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: HistoryAdapter

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
        setupSearchField()
    }

    private fun setupSearchAdapter() {
        searchAdapter =
            SearchAdapter { cocktail ->
                val intent = CocktailActivity.newIntent(requireContext(), cocktail.id)
                startActivity(intent)
            }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
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
                onSearchResults = { cocktails ->
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.llNoResults.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    searchAdapter.updateCocktails(cocktails)
                    Toast.makeText(context, getString(R.string.found_n_cocktails, cocktails.size), Toast.LENGTH_SHORT).show()
                },
                onNoResults = {
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    binding.llNoResults.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    searchAdapter.updateCocktails(emptyList())
                },
                onError = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    binding.rvSearchHistory.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                    binding.llNoResults.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
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

    private fun setupSearchField() {
        binding.etSearch.addTextChangedListener(
            object : android.text.TextWatcher {
                private var searchJob: Runnable? = null

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {}

                override fun afterTextChanged(s: android.text.Editable?) {
                    val query = s?.toString()?.trim() ?: ""

                    searchJob?.let { binding.etSearch.removeCallbacks(it) }

                    if (query.isEmpty()) {
                        searchFragmentManager.clearSearchResults()
                        showSearchHistory()
                        return
                    }

                    // Hide history when typing
                    hideSearchHistory()

                    searchJob =
                        Runnable {
                            val searchType = searchFragmentManager.getCurrentSearchType()
                            searchFragmentManager.searchCocktails(query, searchType)
                        }

                    searchJob?.let { binding.etSearch.postDelayed(it, 500) }
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
        // Show history when returning to fragment
        showSearchHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
