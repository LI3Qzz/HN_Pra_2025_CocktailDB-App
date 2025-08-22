package com.sun.cocktaildb.screen.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.cocktaildb.databinding.FragmentFavoriteBinding
import com.sun.cocktaildb.utils.base.BaseFragment

class FavoriteFragment : BaseFragment(), FavoriteView {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: FavoritePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        presenter = FavoritePresenter()
        presenter.setView(this)
        showEmptyState()
    }

    private fun showEmptyState() {
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.rvFavorites.visibility = View.GONE
    }

    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showError(message: String) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}