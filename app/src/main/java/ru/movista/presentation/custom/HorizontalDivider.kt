package ru.movista.presentation.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.movista.R

class HorizontalDivider(
    context: Context,
    private var divider: Drawable? = null,
    @DimenRes leftMarginRes: Int = 0,
    @DimenRes rightMarginRes: Int = 0
) : RecyclerView.ItemDecoration() {
    private var leftMargin: Int = 0
    private var rightMargin: Int = 0

    init {
        if (divider == null) {
            divider = ContextCompat.getDrawable(context, R.drawable.default_divider)
        }

        if (leftMarginRes != 0) {
            leftMargin = context.resources.getDimensionPixelSize(leftMarginRes)
        }

        if (rightMarginRes != 0) {
            rightMargin = context.resources.getDimensionPixelSize(rightMarginRes)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            return
        }

        divider?.let { outRect.top = it.intrinsicHeight }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let { divider ->
            val dividerLeft = leftMargin
            val dividerRight = parent.width - rightMargin
            val childCount = parent.childCount

            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + divider.intrinsicHeight

                divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                divider.draw(canvas)
            }
        }
    }
}