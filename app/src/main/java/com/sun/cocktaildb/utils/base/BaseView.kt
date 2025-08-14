package com.sun.cocktaildb.utils.base

interface BaseView {
    fun showLoading()

    fun hideLoading()

    fun showError(message: String)
}
