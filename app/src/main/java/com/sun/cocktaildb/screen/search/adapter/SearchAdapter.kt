package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemSearchCocktailBinding
import com.sun.cocktaildb.utils.ImageLoader
import com.sun.cocktaildb.utils.Constants

class SearchAdapter(
    private val onCocktailClicked: (Cocktail) -> Unit,
    private val onFavoriteClickListener: (Cocktail, Boolean) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var cocktails: MutableList<Cocktail> = mutableListOf()
    private var currentSearchQuery: String = ""

    fun updateCocktailFavoriteStatus(cocktailId: String, isFavorite: Boolean) {
        val index = cocktails.indexOfFirst { it.id == cocktailId }
        if (index != -1) {
            val updatedCocktail = cocktails[index].copy(isFavorite = isFavorite)
            cocktails[index] = updatedCocktail
            notifyItemChanged(index)
        }
    }
    
    fun getCurrentCocktails(): List<Cocktail> {
        return cocktails.toList()
    }

    fun updateCocktails(newCocktails: List<Cocktail>, searchQuery: String = "") {
        cocktails.clear()
        cocktails.addAll(newCocktails)
        currentSearchQuery = searchQuery
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchCocktailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(cocktails[position])
    }

    override fun getItemCount(): Int = cocktails.size

    inner class SearchViewHolder(
        private val binding: ItemSearchCocktailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCocktailClicked(cocktails[position])
                }
            }

            binding.ivFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cocktail = cocktails[position]
                    val newFavoriteStatus = !cocktail.isFavorite
                    onFavoriteClickListener(cocktail, newFavoriteStatus)
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.tvCocktailName.text = cocktail.name
            binding.tvCocktailDescription.text = cocktail.description
            
            // Load cocktail image
            if (!cocktail.imageUrl.isNullOrEmpty()) {
                ImageLoader.loadImage(
                    binding.ivCocktailImage,
                    cocktail.imageUrl,
                    R.drawable.placeholder
                )
            } else {
                binding.ivCocktailImage.setImageResource(R.drawable.placeholder)
            }

            // Set favorite button state
            binding.ivFavorite.isSelected = cocktail.isFavorite
            binding.ivFavorite.setImageResource(
                if (cocktail.isFavorite) R.drawable.ic_favorite_filled_black_24dp
                else R.drawable.ic_favorite_border_black_24dp
            )
        }
    }
}
