package com.sun.cocktaildb.data.model

data class Cocktail(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: String,
    val category: String,
    val isFavorite: Boolean = false,
)
