package ru.movista.presentation.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.movista.R
import ru.movista.utils.reportNullFieldError

object SpannableUtils {

    fun setLinkMovement(
        textView: TextView,
        textResId: Int,
        startPos: Int,
        endPos: Int,
        action: () -> (Unit)
    ) {
        val context = textView.context

        val text = context.getString(textResId)
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                action.invoke()
            }

            override fun updateDrawState(paint: TextPaint) {
                super.updateDrawState(paint)

                context?.let { context ->
                    if (textView.isPressed) {
                        paint.color = ContextCompat.getColor(context, R.color.test)
                    } else {
                        paint.color = context.getAttrColor(R.attr.colorAccent)
                    }

                    textView.invalidate()
                } ?: reportNullFieldError("context")
            }
        }

        spannableString.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.highlightColor = Color.TRANSPARENT
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}