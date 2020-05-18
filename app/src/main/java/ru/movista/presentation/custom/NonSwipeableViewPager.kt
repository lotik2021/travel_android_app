package ru.movista.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeableViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.viewpager.widget.ViewPager(context, attrs) {
    private var isEnabledPaging: Boolean = true

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (isEnabledPaging)
            return super.onInterceptTouchEvent(event)

        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabledPaging)
            return super.onTouchEvent(event)

        return false
    }

    fun setPagingEnabled(enabled: Boolean) {
        isEnabledPaging = enabled
    }
}