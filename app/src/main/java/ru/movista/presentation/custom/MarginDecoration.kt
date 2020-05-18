package ru.movista.presentation.custom

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class MarginDecoration(
    context: Context,
    @DimenRes topMarginRes: Int = 0,
    @DimenRes bottomMarginRes: Int = 0
) : RecyclerView.ItemDecoration() {
    private var topMargin: Int = 0
    private var bottomMargin: Int = 0

    constructor(context: Context, verticalMarginRes: Int) : this(
        context,
        verticalMarginRes,
        verticalMarginRes
    )

    init {
        if (topMarginRes != 0) {
            topMargin = context.resources.getDimensionPixelSize(topMarginRes)
        }

        if (bottomMarginRes != 0) {
            bottomMargin = context.resources.getDimensionPixelSize(bottomMarginRes)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        outRect.top = if (position == 0) topMargin else 0
        outRect.bottom = if (position == parent.adapter?.itemCount?.minus(1)) bottomMargin else 0
    }
}
