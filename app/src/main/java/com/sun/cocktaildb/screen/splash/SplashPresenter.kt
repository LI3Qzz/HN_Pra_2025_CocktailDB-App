package com.sun.cocktaildb.screen.splash

import android.os.Handler
import android.os.Looper
import com.sun.cocktaildb.utils.base.BasePresenter

class SplashPresenter : BasePresenter<SplashView> {
    private var view: SplashView? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun setView(view: SplashView?) {
        this.view = view
    }

    override fun onStart() {
        startSplashTimer()
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun startSplashTimer() {
        handler.postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        view?.navigateToHome()
    }
}
