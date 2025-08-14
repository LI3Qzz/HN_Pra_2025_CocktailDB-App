package com.sun.cocktaildb.screen.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail

class PopularCocktailAdapter(
    private val onCocktailClickListener: (Cocktail) -> Unit,
) : RecyclerView.Adapter<PopularCocktailAdapter.CocktailViewHolder>() {
    private val cocktails = mutableListOf<Cocktail>()

    fun updateCocktails(newCocktails: List<Cocktail>) {
        cocktails.clear()
        cocktails.addAll(newCocktails)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CocktailViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_popular_cocktail, parent, false)
        return CocktailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CocktailViewHolder,
        position: Int,
    ) {
        holder.bind(cocktails[position])
    }

    override fun getItemCount(): Int = cocktails.size

    inner class CocktailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCocktail: ImageView = itemView.findViewById(R.id.iv_cocktail)
        private val tvCocktailName: TextView = itemView.findViewById(R.id.tv_cocktail_name)
        private val tvCocktailDescription: TextView = itemView.findViewById(R.id.tv_cocktail_description)

        fun bind(cocktail: Cocktail) {
            tvCocktailName.text = cocktail.name
            tvCocktailDescription.text = cocktail.description
            ivCocktail.setImageResource(R.drawable.placeholder)

            itemView.setOnClickListener {
                onCocktailClickListener(cocktail)
            }
        }
    }
}
