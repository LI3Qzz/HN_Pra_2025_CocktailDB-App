package com.sun.cocktaildb.screen.authenticate.register

import com.sun.cocktaildb.utils.base.BasePresenter
import com.sun.cocktaildb.utils.base.BaseView

interface RegisterContract {
    interface View : BaseView {
        fun onRegisterSuccess()

        fun onRegisterFailed(message: String)

        fun showEmailInvalid(message: String)

        fun showPasswordInvalid(message: String)

        fun showConfirmPasswordInvalid(message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun doRegister(
            email: String,
            password: String,
        )
    }
}
