package ru.movista.data.source.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeParseException
import ru.movista.data.source.local.models.*
import ru.movista.domain.model.tickets.ComfortType

class ComfortTypeAdapter {
    @FromJson
    fun fromJson(data: String?): ComfortType? = ComfortType.getById(data)

    @ToJson
    fun toJson(data: ComfortType?): String? = data?.value
}

class PathGroupStateAdapter {
    @FromJson
    fun fromJson(data: String?): PathGroupState = PathGroupState.getById(data)

    @ToJson
    fun toJson(data: PathGroupState): String = data.id
}

class TripTypeAdapter {
    @FromJson
    fun fromJson(data: String): TripType = TripType.getById(data)

    @ToJson
    fun toJson(data: TripType): String = data.id
}

class ObjectTripTypeAdapter {
    @FromJson
    fun fromJson(data: String): ObjectTripType = ObjectTripType.getById(data)

    @ToJson
    fun toJson(data: ObjectTripType): String = data.type
}

class PriceSegmentTypeAdapter {
    @FromJson
    fun fromJson(data: String): PriceSegmentType = PriceSegmentType.getById(data)

    @ToJson
    fun toJson(data: PriceSegmentType): String = data.type
}

class ObjectServiceTypeAdapter {
    @FromJson
    fun fromJson(data: String): ObjectServiceType = ObjectServiceType.getById(data)

    @ToJson
    fun toJson(data: ObjectServiceType): String = data.type
}

class TrainCoachTypeAdapter {
    @FromJson
    fun fromJson(data: String?): TrainCoachType? = TrainCoachType.getById(data)

    @ToJson
    fun toJson(data: TrainCoachType): String = data.id
}

class ZonedDateTimeAdapter {
    @FromJson
    fun fromJson(date: String): ZonedDateTime? {
        return try {
            ZonedDateTime.parse(date)
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    @ToJson
    fun toJson(zonedDateTime: ZonedDateTime?): String? = zonedDateTime?.toString()
}

class DateTimeAdapter {
    @FromJson
    fun fromJson(date: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(date)
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    @ToJson
    fun toJson(dateTime: LocalDateTime?): String? = dateTime?.toString()
}

class DateAdapter {
    @FromJson
    fun fromJson(date: String): LocalDate? {
        return try {
            LocalDate.parse(date)
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    @ToJson
    fun toJson(date: LocalDate?): String? = date.toString()
}
