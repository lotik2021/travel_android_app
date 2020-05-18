package ru.movista.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.constraintlayout.widget.ConstraintLayout

class CheckableConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), Checkable {
    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    private val checkableSet = hashSetOf<Checkable>()
    private var checked: Boolean = false
    private var checkedChangeListener: ((CheckableConstraintLayout, Boolean) -> (Unit))? = null
    var isRadioLayout: Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        val allChildCount = childCount
        for (i in 0 until allChildCount) {
            val v = getChildAt(i)
            if (v is Checkable) {
                checkableSet.add(v)
            }
        }
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun setChecked(checked: Boolean) {
        if (checked == this.checked) {
            return
        }

        this.checked = checked
        for (checkable in checkableSet) {
            checkable.isChecked = checked
        }

        refreshDrawableState()

        checkedChangeListener?.invoke(this, checked)
    }

    override fun toggle() {
        if (isRadioLayout && isChecked) {
            return
        }
        isChecked = !checked
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    fun setOnCheckedChangeListener(listener: (CheckableConstraintLayout, Boolean) -> (Unit)) {
        checkedChangeListener = listener
    }
}