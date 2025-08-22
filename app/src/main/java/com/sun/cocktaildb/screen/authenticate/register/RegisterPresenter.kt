package com.sun.cocktaildb.screen.authenticate.register

import com.sun.cocktaildb.data.repository.impl.FirebaseAuthImplement
import com.sun.cocktaildb.data.repository.remote.AuthRepository

class RegisterPresenter constructor(
    private val authRepository: AuthRepository = FirebaseAuthImplement(),
) : RegisterContract.Presenter {
    private var view: RegisterContract.View? = null

    init {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun setView(view: RegisterContract.View?) {
        this.view = view
    }

    override fun doRegister(
        email: String,
        password: String,
    ) {
        authRepository.register(email, password) { result ->
            result.fold(
                onSuccess = {
                    view?.onRegisterSuccess()
                },
                onFailure = {
                    view?.onRegisterFailed(result.exceptionOrNull()?.message ?: "Register failed")
                },
            )
        }
    }

    fun validateEmailPattern(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
        return email.matches(emailPattern.toRegex())
    }
}
