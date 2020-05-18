package ru.movista.presentation.custom

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val font: Typeface?) : MetricAffectingSpan() {

    companion object {
        const val RELATIVE_DIGITS_PROPORTION = 0.79f
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        update(textPaint)
    }

    override fun updateDrawState(textPaint: TextPaint) {
        update(textPaint)
    }

    private fun update(textPaint: TextPaint) {
        textPaint.apply {
            val old = typeface
            val oldStyle = old?.style ?: 0

            // keep the style set before
            val font = Typeface.create(font, oldStyle)
            typeface = font
        }
    }
}