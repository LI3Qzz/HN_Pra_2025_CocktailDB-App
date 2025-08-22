package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemSearchCocktailBinding
import com.sun.cocktaildb.utils.ImageLoader
import com.sun.cocktaildb.utils.Constants

class SearchAdapter(
    private val onCocktailClickListener: (Cocktail) -> Unit,
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
    
    fun updateCocktails(newCocktails: List<Cocktail>) {
        cocktails.clear()
        cocktails.addAll(newCocktails)
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
                    onCocktailClickListener(cocktails[position])
                }
            }
            
            binding.ivFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cocktail = cocktails[position]
                    // Play animation
                    val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.favorite_scale)
                    binding.ivFavorite.startAnimation(animation)
                    
                    val newFavoriteStatus = !cocktail.isFavorite
                    onFavoriteClickListener(cocktail, newFavoriteStatus)
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.apply {
                // Highlight search terms in cocktail name if searching by name
                if (currentSearchQuery.isNotEmpty()) {
                    tvCocktailName.text = highlightSearchTerms(cocktail.name, currentSearchQuery)
                } else {
                    tvCocktailName.text = cocktail.name
                }

                // Show category in description
                val categoryText = "Category: ${cocktail.category}"
                tvCocktailDescription.text = categoryText

                // Load image using ImageLoader utility
                val imageUrl = cocktail.imageUrl
                if (imageUrl.isNotEmpty() && imageUrl != Constants.PLACEHOLDER_IMAGE_URL) {
                    ImageLoader.loadImage(ivCocktailImage, imageUrl, R.drawable.placeholder)
                } else {
                    ivCocktailImage.setImageResource(R.drawable.placeholder)
                }
                
                // Set favorite icon based on cocktail's favorite status
                updateFavoriteIcon(cocktail.isFavorite)
            }
        }
        
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled_black_24dp)
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
        }

        // Highlight search terms in text (prioritize left-to-right matches)
        private fun highlightSearchTerms(text: String, query: String): android.text.SpannableString {
            val spannableString = android.text.SpannableString(text)
            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()
            
            var startIndex = 0
            while (true) {
                val index = lowerText.indexOf(lowerQuery, startIndex)
                if (index == -1) break
                
                spannableString.setSpan(
                    android.text.style.BackgroundColorSpan(android.graphics.Color.YELLOW),
                    index,
                    index + query.length,
                    android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                
                startIndex = index + 1
            }
            
            return spannableString
        }
    }
}
