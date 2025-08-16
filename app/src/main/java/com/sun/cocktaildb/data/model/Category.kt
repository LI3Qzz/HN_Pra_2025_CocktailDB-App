package com.sun.cocktaildb.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
) : Parcelable



