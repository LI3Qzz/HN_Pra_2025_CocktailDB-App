package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail


import com.sun.cocktaildb.databinding.ItemSearchCocktailBinding
import com.sun.cocktaildb.utils.ImageLoader


class SearchAdapter(
    private val onItemClick: (Cocktail) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var items: List<Cocktail> = emptyList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_cocktail_name)
        val description: TextView = itemView.findViewById(R.id.tv_cocktail_description)
        val image: ImageView = itemView.findViewById(R.id.iv_cocktail_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_cocktail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.description.text = item.description
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size


    fun updateCocktails(newItems: List<Cocktail>) {
        items = newItems
        notifyDataSetChanged()

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCocktailClicked(cocktails[position])
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
