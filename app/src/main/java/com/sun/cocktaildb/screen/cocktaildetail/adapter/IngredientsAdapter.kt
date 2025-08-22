package com.sun.cocktaildb.screen.cocktaildetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.databinding.ItemIngredientBinding
import com.sun.cocktaildb.databinding.ItemIngredientsBinding

class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {
    private var ingredients: List<String> = emptyList()

    fun updateIngredients(newIngredients: List<String>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): IngredientViewHolder {
        val binding =
            ItemIngredientsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int,
    ) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int = ingredients.size

    inner class IngredientViewHolder(
        private val binding: ItemIngredientsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: String) {
            binding.apply {
                // Split ingredient into name and measure if possible
                val parts = ingredient.trim().split(" ", limit = 2)
                if (parts.size >= 2) {
                    title.text = parts[1] // Ingredient name
                    quantity.text = parts[0] // Measure
                } else {
                    title.text = ingredient
                    quantity.text = "1 oz" // Default measure
                }
            }
        }
    }
} 
