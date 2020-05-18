package ru.movista.presentation.custom

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.layout_expandable.view.*
import ru.movista.R
import ru.movista.presentation.utils.isVisible
import ru.movista.presentation.utils.setDrawables
import ru.movista.presentation.utils.setGone
import ru.movista.presentation.utils.setVisible

class ExpandableLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    var actions: ((Boolean) -> Unit)? = null

    init {
        inflate(context, R.layout.layout_expandable, this)
        orientation = VERTICAL

        layoutTransition = LayoutTransition()

        expandable_title.setOnClickListener {
            if (expandable_content.isVisible()) {
                expandable_content.setGone()
                expandable_title.setDrawables(right = R.drawable.ic_bottom_arrow)
                actions?.invoke(false)
            } else {
                expandable_content.setVisible()
                expandable_title.setDrawables(right = R.drawable.ic_top_arrow)
                actions?.invoke(true)
            }
        }
    }

    fun getTitleTextView(): TextView {
        return expandable_title
    }

    fun setTitle(@StringRes textReId: Int) {
        expandable_title.setText(textReId)
    }

    fun setTitle(text: String) {
        expandable_title.text = text
    }

    fun setExpandableView(@LayoutRes expandableViewId: Int) {
        expandable_content.addView(LayoutInflater.from(context).inflate(expandableViewId, null, false))
        invalidate()
    }
}