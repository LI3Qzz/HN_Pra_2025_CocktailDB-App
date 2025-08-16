package com.sun.cocktaildb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,

    val description: String,
    val imageUrl: String,
) : Parcelable



