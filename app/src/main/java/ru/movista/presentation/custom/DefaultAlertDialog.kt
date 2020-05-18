package ru.movista.presentation.custom

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import ru.movista.R
import ru.movista.presentation.utils.getAttrColor

class DefaultAlertDialog(private val context: Context) {
    private var dialog: AlertDialog = AlertDialog.Builder(context).create()

    fun setTitle(@StringRes resId: Int): DefaultAlertDialog {
        dialog.setTitle(resId)
        return this
    }

    fun setMessage(@StringRes resId: Int): DefaultAlertDialog {
        dialog.setMessage(context.getString(resId))
        return this
    }

    fun setMessage(message: String): DefaultAlertDialog {
        dialog.setMessage(message)
        return this
    }

    fun setPositiveButton(@StringRes resId: Int, action: () -> Unit): DefaultAlertDialog {
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(resId)) { _, _ -> action.invoke() }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(context.getAttrColor(R.attr.colorAccent))
        return this
    }

    fun setNegativeButton(@StringRes resId: Int, action: () -> Unit): DefaultAlertDialog {
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(resId)) { _, _ -> action.invoke() }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(context.getAttrColor(R.attr.colorAccent))
        return this
    }

    fun setOnDismissListener(action: () -> Unit): DefaultAlertDialog {
        dialog.setOnDismissListener { action.invoke() }
        return this
    }

    fun setView(view: View): DefaultAlertDialog {
        dialog.setView(view)
        return this
    }

    fun setCancelable(cancelable: Boolean): DefaultAlertDialog {
        dialog.setCancelable(cancelable)
        return this
    }

    fun build(): AlertDialog {
        return dialog
    }
}