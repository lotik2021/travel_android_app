package ru.movista.presentation.intro.pager

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import ru.movista.R
import java.util.*

class ImagePagerIndicator(pagerIndicator: LinearLayout, dotsCount: Int) :
    androidx.viewpager.widget.ViewPager.OnPageChangeListener {
    private lateinit var dots: MutableList<ImageView>

    init {
        setPagerIndicator(pagerIndicator, dotsCount)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        for (i in dots.indices) {
            resetPagerIndicator(dots[i])
        }
        setPagerIndicatorActive(dots[position])
    }

    private fun setPagerIndicator(pagerIndicator: LinearLayout, dotsCount: Int) {
        pagerIndicator.removeAllViews()
        if (dotsCount < 2) {
            return
        }

        val resources = pagerIndicator.context.resources
        val indicatorSize = resources.getDimensionPixelSize(R.dimen.pager_indicator_size)
        val indicatorMargin = resources.getDimensionPixelSize(R.dimen.pager_indicator_margin)

        dots = ArrayList()
        for (i in 0 until dotsCount) {
            val indicator = ImageView(pagerIndicator.context)
            val indicatorParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(indicatorSize, indicatorSize)
            indicatorParams.marginEnd = indicatorMargin
            indicator.layoutParams = indicatorParams
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    pagerIndicator.context,
                    R.drawable.default_page_indicator
                )
            )

            resetPagerIndicator(indicator)
            dots.add(indicator)
            pagerIndicator.addView(dots[i])
        }
        setPagerIndicatorActive(dots[0])
    }

    private fun setPagerIndicatorActive(imageView: ImageView) {
        imageView.alpha = 1f
    }

    private fun resetPagerIndicator(imageView: ImageView) {
        imageView.alpha = 0.4f
    }
}