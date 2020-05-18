package ru.movista.presentation.utils

import android.os.Handler
import android.os.Looper

object HandlersHolder {
    val chatMessagesHandler: Handler = Handler(Looper.getMainLooper())
    val userActionsHandler: Handler = Handler(Looper.getMainLooper())
    val commonHandler: Handler = Handler(Looper.getMainLooper())

    fun removeAllCallbacks() {
        chatMessagesHandler.removeCallbacksAndMessages(null)
        userActionsHandler.removeCallbacksAndMessages(null)
        commonHandler.removeCallbacksAndMessages(null)
    }
}