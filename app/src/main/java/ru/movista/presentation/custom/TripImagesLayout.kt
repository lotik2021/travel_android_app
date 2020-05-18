package ru.movista.presentation.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import ru.movista.R
import ru.movista.data.source.local.models.TripType
import ru.movista.presentation.utils.toPx

class TripImagesLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    companion object {
        private const val DEFAULT_IMAGE_SIZE = 24
        private const val DEFAULT_ARROW_IMAGE_SIZE = 14
    }

    private var setupImageAction: ((ImageView, TripType) -> Unit)? = null
    private val transportImageSize: Int

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TripImagesLayout)
        transportImageSize = typedArray.getDimension(
            R.styleable.TripImagesLayout_imageSize,
            DEFAULT_IMAGE_SIZE.toPx().toFloat()
        ).toInt()

        typedArray.recycle();
    }

    fun setSingleGroupItems(
        items: List<TripType>,
        action: ((ImageView, TripType) -> Unit)? = null
    ) {
        setItems(arrayListOf(items), action)
    }

    fun setItems(
        items: List<List<TripType>>,
        action: ((ImageView, TripType) -> Unit)? = null
    ) {
        this.setupImageAction = action

        var lastImageView: ImageView? = null

        items.forEachIndexed { transportGroupIndex, transportVariants ->
            transportVariants.forEachIndexed { transportIndex, transport ->
                if (transportGroupIndex == 0 && transportIndex == 0) {
                    lastImageView = createFirstTransportImage(
                        "transport_$transportGroupIndex$transportIndex",
                        transport,
                        transportVariants.size - transportIndex
                    )
                } else if (transportIndex == 0) {
                    lastImageView?.let { lastImage ->
                        val arrowImageView = createTransportArrow(
                            "arrow_$transportGroupIndex$transportIndex",
                            lastImage,
                            transportVariants.size - transportIndex
                        )

                        lastImageView = createMiddleTransportImage(
                            "transport_$transportGroupIndex$transportIndex",
                            transport,
                            arrowImageView,
                            transportVariants.size - transportIndex
                        )
                    }
                } else {
                    lastImageView?.let { lastImage ->
                        lastImageView = createLastTransportImage(
                            "transport_$transportGroupIndex$transportIndex",
                            transport,
                            lastImage,
                            transportVariants.size - transportIndex
                        )
                    }
                }
            }
        }
    }

    private fun createFirstTransportImage(
        id: String,
        tripType: TripType,
        elevationIndex: Int
    ): ImageView {
        val imageView: ImageView

        if (setupImageAction == null) {
            val image = ContextCompat.getDrawable(context, tripType.iconResId)
            imageView = createImageView(id = id, drawable = image, elevationIndex = elevationIndex)
        } else {
            imageView = createImageView(id = id, elevationIndex = elevationIndex)
            setupImageAction?.invoke(imageView, tripType)
        }

        val set = ConstraintSet()
        addView(imageView)
        set.clone(this)
        set.connect(imageView.id, ConstraintSet.START, this.id, ConstraintSet.START)
        set.connect(imageView.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        set.applyTo(this)
        return imageView
    }

    private fun createTransportArrow(
        id: String,
        lastImageView: ImageView,
        elevationIndex: Int
    ): ImageView {
        val arrowImage = ContextCompat.getDrawable(
            context,
            R.drawable.ic_right_arrow
        )
        val arrowImageView = createImageView(
            id = id,
            size = DEFAULT_ARROW_IMAGE_SIZE.toPx(),
            drawable = arrowImage,
            elevationIndex = elevationIndex
        )

        val set = ConstraintSet()
        addView(arrowImageView)
        set.clone(this)
        set.connect(
            arrowImageView.id,
            ConstraintSet.BOTTOM,
            lastImageView.id,
            ConstraintSet.BOTTOM
        )
        set.connect(
            arrowImageView.id,
            ConstraintSet.START,
            lastImageView.id,
            ConstraintSet.END
        )
        set.connect(arrowImageView.id, ConstraintSet.TOP, lastImageView.id, ConstraintSet.TOP)
        set.applyTo(this)
        return arrowImageView
    }

    private fun createMiddleTransportImage(
        id: String,
        tripType: TripType,
        arrowImageView: ImageView,
        elevationIndex: Int
    ): ImageView {
        val imageView: ImageView

        if (setupImageAction == null) {
            val image = ContextCompat.getDrawable(context, tripType.iconResId)
            imageView = createImageView(id = id, drawable = image, elevationIndex = elevationIndex)
        } else {
            imageView = createImageView(id = id, elevationIndex = elevationIndex)
            setupImageAction?.invoke(imageView, tripType)
        }

        val set = ConstraintSet()
        addView(imageView)
        set.clone(this)
        set.connect(
            imageView.id,
            ConstraintSet.START,
            arrowImageView.id,
            ConstraintSet.END
        )
        set.connect(imageView.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        set.applyTo(this)
        return imageView
    }

    private fun createLastTransportImage(
        id: String,
        tripType: TripType,
        lastImageView: ImageView,
        elevationIndex: Int
    ): ImageView {
        val imageView: ImageView

        if (setupImageAction == null) {
            val image = ContextCompat.getDrawable(context, tripType.iconResId)
            imageView = createImageView(id = id, drawable = image, elevationIndex = elevationIndex)
        } else {
            imageView = createImageView(id = id, elevationIndex = elevationIndex)
            setupImageAction?.invoke(imageView, tripType)
        }


        val set = ConstraintSet()
        addView(imageView)
        set.clone(this)
        set.connect(
            imageView.id,
            ConstraintSet.START,
            lastImageView.id,
            ConstraintSet.END
        )
        set.connect(
            imageView.id,
            ConstraintSet.END,
            lastImageView.id,
            ConstraintSet.END
        )

        set.connect(imageView.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
        set.applyTo(this)
        return imageView
    }

    private fun createImageView(
        id: String,
        size: Int = transportImageSize,
        drawable: Drawable? = null,
        elevationIndex: Int
    ): ImageView {
        return ImageView(context).apply {
            val lp = LayoutParams(size, size)
            this.id = id.hashCode()
            layoutParams = lp
            elevation = elevationIndex.toFloat()
            drawable?.let { setImageDrawable(drawable) }
        }
    }
}