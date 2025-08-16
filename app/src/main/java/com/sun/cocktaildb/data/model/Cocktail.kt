package com.sun.cocktaildb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cocktail(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: String,
    val category: String,
    val isFavorite: Boolean = false,
) : Parcelable
