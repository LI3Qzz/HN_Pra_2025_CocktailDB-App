package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemSearchCocktailBinding
import com.sun.cocktaildb.utils.ImageLoader

class SearchAdapter(

    private val onCocktailClickListener: (Cocktail) -> Unit,
    private val onFavoriteClickListener: (Cocktail, Boolean) -> Unit

    private val onCocktailClicked: (Cocktail) -> Unit

) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var cocktails: List<Cocktail> = emptyList()


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
    
    fun updateCocktails(newCocktails: List<Cocktail>) {
        cocktails.clear()
        cocktails.addAll(newCocktails)

    fun updateCocktails(newCocktails: List<Cocktail>) {
        cocktails = newCocktails

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
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCocktailClickListener(cocktails[position])
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.apply {
                tvCocktailName.text = cocktail.name

                // Display ingredients in the format shown in the image
                val ingredientsText = getIngredientsText(cocktail)
                tvCocktailDescription.text = ingredientsText

                // Load image using ImageLoader utility
                val imageUrl = cocktail.imageUrl
                if (imageUrl.isNotEmpty() && imageUrl != "https://example.com/placeholder.jpg") {
                    ImageLoader.loadImage(ivCocktailImage, imageUrl, R.drawable.placeholder)
                } else {
                    ivCocktailImage.setImageResource(R.drawable.placeholder)
                }
            }
        }

        // Extract complex logic into separate method for better readability and testability
        private fun getIngredientsText(cocktail: Cocktail): String {
            return if (cocktail.ingredients.isNotEmpty() &&
                cocktail.ingredients.first() != "Ingredients not available") {
                // Show first ingredient with its measure if available
                val firstIngredient = cocktail.ingredients.first()
                if (firstIngredient.contains(" ")) {
                    // If ingredient already has measure, use as is
                    firstIngredient
                } else {
                    // Add default measure
                    "1/2 oz $firstIngredient"
                }
            } else {
                "1/2 oz Ingredients not available"
            }
        }
    }
}
