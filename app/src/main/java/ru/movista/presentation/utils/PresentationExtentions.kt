package ru.movista.presentation.utils

import android.app.Activity
import android.content.*
import android.content.ClipboardManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.*
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import ru.movista.App
import ru.movista.R
import ru.movista.presentation.custom.CustomTypefaceSpan
import ru.movista.utils.toDefaultLowerCase
import timber.log.Timber
import java.util.*

fun View.setGone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.setVisible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.setInVisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}

fun View.hideSoftKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showSoftKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Context?.openInCustomBrowser(link: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    this?.let { customTabsIntent.launchUrl(it, Uri.parse(link)) }
}

fun Context?.goToCurrentApplicationSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", this?.packageName, null)
    )

    this?.startActivity(intent)
}

fun Context.openInMailApplication(
    whom: String,
    title: String,
    content: String,
    createChooserTitle: String,
    extraStream: Uri?
) {
    val mailto = "mailto:"

    val emailIntent = if (extraStream == null) {
        Intent(Intent.ACTION_SENDTO)
    } else {
        Intent(Intent.ACTION_SEND)
    }

    emailIntent.apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(whom))
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }

    if (extraStream != null) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse(mailto) }
        emailIntent.apply {
            selector = selectorIntent
            putExtra(Intent.EXTRA_STREAM, extraStream)
        }
    } else {
        emailIntent.data = Uri.parse(mailto)
    }

    try {
        startActivity(Intent.createChooser(emailIntent, createChooserTitle))
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    }
}

fun Context.openPhoneDial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse(phone) }
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    }
}

fun Context.getPlaceByCoordinates(
    lat: Double,
    lon: Double,
    maxResults: Int = 1,
    onCompleteAction: (List<Address>?) -> Unit
) {
    val geocoder = Geocoder(this, Locale.getDefault())

    val result: List<Address>? = try {
        geocoder.getFromLocation(lat, lon, maxResults)
    } catch (e: Exception) {
        null
    }

    doAsync {

        val result: List<Address>? = try {
            geocoder.getFromLocation(lat, lon, maxResults)
        } catch (e: Exception) {
            null
        }

        onComplete {
            onCompleteAction.invoke(result)
        }
    }
}

@ColorInt
fun Context.getAttrColor(@AttrRes colorAttrRes: Int): Int {
    val typedArray = obtainStyledAttributes(TypedValue().data, intArrayOf(colorAttrRes))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}

fun Resources.getImagePathFromResourceName(resourceName: String): Int? {
    val imagePath = this.getIdentifier(
        resourceName,
        "drawable",
        App.PACKAGE_NAME
    )

    return if (imagePath != 0) imagePath else null
}

fun EditText.onTextChangedListener(action: (s: CharSequence?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action.invoke(s)
        }
    })
}

fun View.shortSnackbar(message: String) {
    with(Snackbar.make(this, message, Snackbar.LENGTH_SHORT)) {
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.blue))
        show()
    }
}

fun View.longSnackbar(message: String) {
    with(Snackbar.make(this, message, Snackbar.LENGTH_LONG)) {
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.blue))
        show()
    }
}

fun View.infiniteSnackBar(
    message: String,
    actionMessage: String? = null,
    action: View.OnClickListener? = null
): Snackbar {
    with(Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)) {
        actionMessage?.let { actionMessage ->
            view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.blue))
            setActionTextColor(context.getAttrColor(R.attr.colorAccent))
            setAction(actionMessage, action)
        }
        show()
        return this
    }
}

fun TextView.setDrawables(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun Context.copyToClipboard(message: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clipData = ClipData.newPlainText("Text", message)
    clipboardManager?.primaryClip = clipData
}

fun View.animateOnPress() {
    this.animate()
        .scaleX(0.95f)
        .scaleY(0.95f)
        .setDuration(75L)
        .start()
}

fun View.animateOnRelease() {
    this.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(75L)
        .start()
}

fun View.setScaleClick(action: (() -> Unit)? = null) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animateOnPress()
            MotionEvent.ACTION_CANCEL -> animateOnRelease()
            MotionEvent.ACTION_UP -> {
                animateOnRelease()

                action?.invoke()
            }
            else -> { }
        }
        return@setOnTouchListener true
    }
}

fun String.isSuccessPaymentUrl(): Boolean {
    return this.contains("movista") && this.contains("payment") && this.contains("success")
}

fun postOnMainThread(runnable: () -> Unit) {
    Handler(Looper.getMainLooper()).post(runnable)
}

fun postOnMainThreadWithDelay(delay: Long, runnable: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(runnable, delay)
}

fun TextView.setSmallCaps(@StringRes inTextRes: Int) {
    setSmallCaps(context.getString(inTextRes))
}

fun TextView.setSmallCaps(inText: String) {
    val text = inText.toDefaultLowerCase()
    val spannableString = SpannableStringBuilder()
    val stringBuilder = StringBuilder()

    text.forEachIndexed { index, char ->
        stringBuilder.append(char)

        if (char.isLetter() || char.isWhitespace()) {
            if (index >= text.length - 1 || !text[index + 1].isLetter() || !text[index + 1].isWhitespace()) {
                spannableString.append(stringBuilder.toString())
                stringBuilder.setLength(0)
            }
        } else {
            if (index >= text.length - 1 || text[index + 1].isLetter() || text[index + 1].isWhitespace()) {
                val spannable = SpannableString(stringBuilder.toString()).apply {
                    setSpan(RelativeSizeSpan(CustomTypefaceSpan.RELATIVE_DIGITS_PROPORTION), 0, length, 0)
                }
                spannableString.append(spannable)
                stringBuilder.setLength(0)
            }
        }
    }

    fontFeatureSettings = "smcp"
    setText(spannableString)
}

// todo imageUrlForSmallVersion - это костыль, чтобы была поддержка картинок для android api 22 и ниже (android api 22 и ниже не поддерживает svg)
fun ImageView.inject(url: String?, imageUrlForSmallVersion: String?) {
    val resultUrl = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
        imageUrlForSmallVersion
    } else {
        url
    }

    Glide.with(this.context)
        .load(resultUrl)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}