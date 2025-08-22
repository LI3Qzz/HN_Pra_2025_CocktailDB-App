package com.sun.cocktaildb.utils.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

const val PREFERENCE_NAME = "cocktail_db"
const val SEARCH_HISTORY = "search_history"

fun Context.saveSetString(setSearchHistory: Set<String>) {
    getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit { putStringSet(SEARCH_HISTORY, setSearchHistory) }
}

fun Context.getSetString(): Set<String>? =
    getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getStringSet(SEARCH_HISTORY, emptySet())

fun Context.saveString(
    key: String,
    value: String,
) {
    getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit { putString(key, value) }
}

fun Context.getString(key: String) {
    getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(key, null)
}
