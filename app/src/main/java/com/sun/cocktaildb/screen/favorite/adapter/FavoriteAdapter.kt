package com.sun.cocktaildb.screen.favorite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemFavoriteCocktailBinding
import com.sun.cocktaildb.utils.ImageLoader
import com.sun.cocktaildb.R

class FavoriteAdapter(
    // MERGED: Keep both parameters for maximum functionality
    private val onCocktailClickListener: (Cocktail) -> Unit,
    private val onFavoriteClickListener: (Cocktail, Boolean) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private var items: List<Cocktail> = emptyList()

    class ViewHolder(val binding: ItemFavoriteCocktailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavoriteCocktailBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        // MERGED: Combined both approaches for maximum compatibility
        // Use new view IDs from HEAD for better UX
        holder.binding.tvCocktailName.text = item.name
        
        // Set cocktail description (ingredients)
        holder.binding.tvCocktailDescription.text = buildString {
            item.ingredients.take(3).forEachIndexed { index, ingredient ->
                append(ingredient)
                if (index < 2) append("\n")
            }
        }
        
        // Load cocktail image with null safety (from HEAD)
        if (!item.imageUrl.isNullOrEmpty()) {
            ImageLoader.loadImage(
                holder.binding.ivCocktailImage,
                item.imageUrl,
                R.drawable.placeholder
            )
        } else {
            holder.binding.ivCocktailImage.setImageResource(R.drawable.placeholder)
        }
        
        // Set favorite icon (always filled since this is favorites list) with red color
        holder.binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled_black_24dp)
        holder.binding.ivFavorite.setColorFilter(holder.binding.root.context.getColor(R.color.colorPrimary))
        
        // Set click listeners
        holder.binding.root.setOnClickListener { 
            onCocktailClickListener(item) 
        }
        
        holder.binding.ivFavorite.setOnClickListener {
            // Remove from favorites
            onFavoriteClickListener(item, false)
        }
        
        // Note: Removed old view ID references as they don't exist in current layout
    }

    override fun getItemCount(): Int = items.size

    fun updateCocktails(newItems: List<Cocktail>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun submit(newItems: List<Cocktail>) {
        items = newItems
        notifyDataSetChanged()
    }

    // MERGED: Keep advanced functionality from HEAD for better UX
    fun getCurrentCocktails(): List<Cocktail> {
        return items.toList()
    }
}
