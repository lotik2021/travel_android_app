package ru.movista.utils

import android.content.Context
import android.content.res.ColorStateList
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.movista.presentation.custom.CustomTypefaceSpan
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

val String.Companion.EMPTY: String
    get() = ""

val String.Companion.DIVIDER: String
    get() = "–"

fun Context.getLowerCaseString(@StringRes resId: Int): String {
    return getString(resId).toLowerCase(Locale.getDefault())
}

fun String.toDefaultLowerCase(): String {
    return toLowerCase(Locale.getDefault())
}

fun <T> Single<T>.schedulersIoToMain(): Single<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.schedulersIoToMain(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun Completable.schedulersIoToMain(): Completable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : Any, R : Any> reportUnexpectedTypeError(expectedType: KClass<T>, actualType: KClass<R>) {
    // todo https://medium.com/@elye.project/the-danger-of-using-class-getsimplename-as-tag-for-fragment-5cdf3a35bfe2
    Timber.e("UnexpectedTypeError: expected ${expectedType.simpleName} but was ${actualType.simpleName}")
}

fun reportNullFieldError(field: String) {
    Timber.e("UnexpectedNullValue: $field must not be null ")
}

fun Double.round(scale: Int): Double {
    return BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()
}

inline fun <reified T : Any> getExpectedItemFromList(selectedIndex: Int, list: List<Any>): T? {
    val selectedItem = list.getOrNull(selectedIndex)

    if (selectedItem == null) {
        Timber.e("index $selectedIndex is out of bounds of list: $list")
        return null
    }

    if (selectedItem !is T) {
        reportUnexpectedTypeError(T::class, selectedItem::class)
        return null
    }
    return selectedItem
}

fun LocalDate.formatByPattern(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))

fun ZonedDateTime.formatByPattern(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))

fun Double.toPriceFormat(): String {
    val div = this - this.toInt()

    return NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
        .apply { minimumFractionDigits = if (div == 0.0) 0 else 2 }
        .format(this)
}

fun Int.toPriceFormat(): String {
    return NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
        .apply { minimumFractionDigits = 0 }
        .format(this)
}

fun Long.toLocalTime() : LocalTime {
    return LocalTime.ofSecondOfDay(TimeUnit.MINUTES.toSeconds(this))
}

fun ZonedDateTime.isAfterMinutes(time: LocalTime?) : Boolean? {
    return toLocalTime()?.withSecond(0)?.withNano(0)?.isAfter(time)
}

fun ZonedDateTime.isBeforeMinutes(time: LocalTime?) : Boolean? {
    return toLocalTime()?.withSecond(0)?.withNano(0)?.isBefore(time)
}

fun Context.inflate(@LayoutRes resId: Int, parent: ViewGroup? = null) : View {
    return LayoutInflater.from(this).inflate(resId, parent, false)
}

fun View.getString(@StringRes resId: Int) : String {
    return resources.getString(resId)
}

fun View.getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return resources.getString(resId, *formatArgs)
}

fun ImageView.setTint(@ColorRes colorResId: Int) {
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
}

fun TextView.setSpan(text: String, startPos: Int, endPos: Int, textSp: Int, @FontRes fontRes: Int = -1) {
    val spannable = SpannableStringBuilder(text)

    if (fontRes != -1) {
        val context = this.context

        ResourcesCompat.getFont(context, fontRes)?.let { firstFont ->
            spannable.setSpan(CustomTypefaceSpan(firstFont), startPos, endPos, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }

    spannable.setSpan(AbsoluteSizeSpan(textSp, true), startPos, endPos, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    this.text = spannable
}

fun View.getDimension(@DimenRes id: Int): Int {
    return this.context.resources.getDimension(id).toInt()
}