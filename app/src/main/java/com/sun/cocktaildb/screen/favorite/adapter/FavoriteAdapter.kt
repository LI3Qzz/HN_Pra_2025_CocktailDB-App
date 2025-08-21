package com.sun.cocktaildb.screen.favorite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail

class FavoriteAdapter(
	private var items: List<Cocktail>,
	private val onItemClick: (Cocktail) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.tv_name)
		val desc: TextView = itemView.findViewById(R.id.tv_desc)
		val thumb: ImageView = itemView.findViewById(R.id.iv_thumb)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_cocktail, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = items[position]
		holder.name.text = item.name
		holder.desc.text = buildString {
			item.ingredients.take(3).forEachIndexed { index, s ->
				append(s)
				if (index < 2) append("\n")
			}
		}
		holder.itemView.setOnClickListener { onItemClick(item) }
	}

	override fun getItemCount(): Int = items.size

	fun submit(newItems: List<Cocktail>) {
		items = newItems
		notifyDataSetChanged()
	}
}
