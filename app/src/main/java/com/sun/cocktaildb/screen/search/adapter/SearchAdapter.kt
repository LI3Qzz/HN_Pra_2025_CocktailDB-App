package com.sun.cocktaildb.screen.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemSearchCocktailBinding

class SearchAdapter(
    private val onCocktailClicked: (Cocktail) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var cocktails: List<Cocktail> = emptyList()

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
                    onCocktailClicked(cocktails[position])
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.apply {
                tvCocktailName.text = cocktail.name
                
                // Display ingredients in the format shown in the image
                val ingredientsText = if (cocktail.ingredients.isNotEmpty()) {
                    "1/2 oz ${cocktail.ingredients.first()}"
                } else {
                    "1/2 oz Ingredients not available"
                }
                tvCocktailDescription.text = ingredientsText
                
                // TODO: Load image using Glide or Picasso
                // For now, using placeholder
                ivCocktailImage.setImageResource(com.sun.cocktaildb.R.drawable.placeholder)
            }
        }
    }
}
