package com.sun.cocktaildb.screen.favorite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.data.model.Cocktail
import com.sun.cocktaildb.databinding.ItemFavoriteCocktailBinding

class FavoriteAdapter(
	private var items: List<Cocktail>,
	private val onItemClick: (Cocktail) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

	class ViewHolder(val binding: ItemFavoriteCocktailBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemFavoriteCocktailBinding.inflate(inflater, parent, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = items[position]
		holder.binding.tvName.text = item.name
		holder.binding.tvDesc.text = buildString {
			item.ingredients.take(3).forEachIndexed { index, s ->
				append(s)
				if (index < 2) append("\n")
			}
		}
		holder.binding.root.setOnClickListener { onItemClick(item) }
	}

	override fun getItemCount(): Int = items.size

	fun submit(newItems: List<Cocktail>) {
		items = newItems
		notifyDataSetChanged()
	}
}
