package com.sun.cocktaildb.screen.authenticate.login

import com.sun.cocktaildb.utils.base.BasePresenter
import com.sun.cocktaildb.utils.base.BaseView

interface LoginContract {
    interface View : BaseView {
        fun showSuccess()

        fun navigateToMain()

        fun getEmail(): String

        fun getPassword(): String

        fun clearInputs()
    }

    interface Presenter : BasePresenter<View> {
        fun login()
    }
}
