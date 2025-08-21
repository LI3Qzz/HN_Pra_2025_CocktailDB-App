package com.sun.cocktaildb.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sun.cocktaildb.R

class LoadingDialog(
    val mContext: Context,
) {
    private var dialog: Dialog? = null
    private var run: Runnable? = null
    private var handler = android.os.Handler(Looper.getMainLooper())

    init {
        dialog = Dialog(mContext)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_loading)
        val window: Window? = dialog?.window
        window?.let {
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            it.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.CENTER

            // Hide system UI (status bar and navigation bar)
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

            it.attributes = layoutParams

            // Set flags to hide system UI
            it.addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            )

            dialog?.setCancelable(false)

            run =
                Runnable {
                    try {
                        if (dialog != null && dialog?.isShowing == true) {
                            dialog?.hide()
                        }
                    } catch (e: Exception) {
                    }
                }
        }
    }

    fun show() {
        dialog?.show()

        dialog?.window?.let { window ->
            // Tell the window not to fit system windows
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars()) // Hide status + nav bar
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        run?.let { handler.postDelayed(it, 90000) }
    }

    fun hide() {
        dialog?.dismiss()

        try {
            run?.let { handler.removeCallbacks(it) }
        } catch (e: Exception) {
        }
    }
}
