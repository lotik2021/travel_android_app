package ru.movista.utils

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

const val GMT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"

fun fromDateToString(dateTime: LocalDateTime, pattern: String = GMT_DATE_TIME_FORMAT): String {
    return ZonedDateTime.of(dateTime, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern))
}

fun durationBetweenTime(startTime: String, endTime: String): String {

    return try {
        val startLocalTime = LocalTime.parse(startTime)
        val endLocalTime = LocalTime.parse(endTime)

        if (endLocalTime.isBefore(startLocalTime)) return String.EMPTY

        minutesToDuration(startLocalTime.until(endLocalTime, org.threeten.bp.temporal.ChronoUnit.MINUTES).toInt())
    } catch (e: Exception) {
        Timber.e(e)
        String.EMPTY
    }

}

fun minutesToDuration(min: Int): String {
    val seconds = min * 60
    return secondsToDuration(seconds)
}

fun secondsToDuration(seconds: Int?): String {
    seconds ?: return String.EMPTY

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return when {
        hours != 0 && minutes != 0 -> "$hours ч $minutes мин"
        hours != 0 -> "$hours ч"
        minutes != 0 -> "$minutes мин"
        else -> "< 1 мин"
    }
}

fun dateTimeToDate(dateTime: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(dateTime)
        zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.YY"))
    } catch (e: Exception) {
        Timber.e(e)
        String.EMPTY
    }
}

fun dateTimeToHHMM(dateTime: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(dateTime)
        zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        Timber.e(e)
        String.EMPTY
    }
}

fun getCurrentTimeZone(): String {
    return try {
        ZonedDateTime.now().zone.toString()
    } catch (e: Exception) {
        Timber.e(e)
        String.EMPTY
    }
}

fun minutesToHHMM(duration: Int): String {
    val hours = Duration.ofMinutes(duration.toLong()).toHours().toInt()
    val minutes = Duration.ofMinutes(duration.toLong())
        .minusHours(hours.toLong())
        .toMinutes()
        .toInt()

    val hoursString = when (hours) {
        in 0..9 -> "0$hours"
        else -> "$hours"
    }

    val minutesString = when (minutes) {
        in 0..9 -> "0$minutes"
        else -> "$minutes"
    }

    return "$hoursString:$minutesString"
}

fun minutesToDDHHMM(duration: Long): String {
    val days = Duration.ofMinutes(duration).toDays()
    val hours = Duration.ofMinutes(duration).minusDays(days).toHours()
    val minutes = Duration.ofMinutes(duration).minusDays(days).minusHours(hours).toMinutes()

    return when {
        days != 0L && minutes != 0L -> "${days}д ${hours}ч ${minutes}м"
        days != 0L && hours != 0L -> "${days}д ${hours}ч"
        days != 0L -> "${days}д"
        hours != 0L && minutes != 0L -> "${hours}ч ${minutes}м"
        hours != 0L -> "${hours}ч"
        else -> "${minutes}м"
    }
}
