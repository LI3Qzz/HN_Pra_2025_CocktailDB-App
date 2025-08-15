package com.sun.cocktaildb.utils.base

interface BasePresenter<T : BaseView> {
    fun onStart()

    fun onStop()

    fun setView(view: T?)
}
