package ru.movista.data.source.local.models

import androidx.annotation.StringRes
import ru.movista.R

enum class TrainCoachType(val id: String, val position: Int, @StringRes val titleResId: Int) {
    SEDENTARY("sedentary", 0, R.string.sedentary),
    THIRD_CLASS_SLEEPER("thirdClassSleeper", 1, R.string.thirdClassSleeper),
    COMPARTMENT("compartment", 2, R.string.compartment),
    LUXURY("luxury", 3, R.string.luxury),
    SOFT("soft", 4, R.string.soft),
    BUSINESS("business", 5, R.string.business),
    FIRST("first", 6, R.string.first),
    MEETING_COUPE("meetingCoupe", 7, R.string.meetingCoupe),
    SHARED("shared", 8, R.string.shared),
    ECONOMY("economy", 9, R.string.economy),
    ECONOMY_PLUS("economyPlus", 10, R.string.economyPlus),
    BUFFET_CAR("buffetCar", 11, R.string.buffetCar),
    OTHER("other", 12, R.string.unknown);

    companion object {
        fun getById(id: String?): TrainCoachType? {
            if (id == null) {
                return null
            }

            values().forEach {
                if (it.id == id) {
                    return it
                }
            }
            return OTHER
        }
    }
}

fun TrainCoachType?.isValid() : Boolean {
    return this != null && this != TrainCoachType.OTHER
}