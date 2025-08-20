package com.sun.cocktaildb.screen.authenticate.login

import com.sun.cocktaildb.data.repository.AuthRepository
import com.sun.cocktaildb.data.repository.impl.FirebaseAuthImplement

class LoginPresenter(
    private val authRepository: AuthRepository = FirebaseAuthImplement(),
) : LoginContract.Presenter {
    private var view: LoginContract.View? = null

    override fun setView(view: LoginContract.View?) {
        this.view = view
    }

    override fun onStart() {
        // Initialize any necessary resources
    }

    override fun onStop() {
        // Clean up resources
    }

    override fun login() {
        view?.showLoading()

        val email = view?.getEmail() ?: ""
        val password = view?.getPassword() ?: ""

        authRepository.login(email, password) { result ->
            result.fold(
                onSuccess = {
                    view?.hideLoading()
                    view?.showSuccess()
                    view?.clearInputs()
                    view?.navigateToMain()
                },
                onFailure = { exception ->
                    view?.hideLoading()
                    view?.showError(exception.message ?: "Login failed")
                },
            )
        }
    }
}
