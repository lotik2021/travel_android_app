package ru.movista.data.source.local.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.movista.R
import java.io.Serializable

enum class TripType(
    val id: String,
    @DrawableRes val iconResId: Int,
    @StringRes val title: Int
) : Serializable {
    TRAIN("train", R.drawable.movista_train, R.string.train_small),
    FLIGHT("flight", R.drawable.movista_flight, R.string.plane_small),
    BUS("bus", R.drawable.movista_bus, R.string.bus_small),
    TRAIN_SUBURBAN("train_suburban", R.drawable.movista_suburban, R.string.suburban_small), // этот тип пока не приходит с сервера
    TAXI("taxi", R.drawable.movista_taxi, R.string.taxi_small),
    OTHER("other", R.drawable.circle_blue, R.string.unknown);

    companion object {
        fun getById(id: String): TripType {
            values().forEach {
                if (it.id == id) {
                    return it
                }
            }
            return OTHER
        }

        fun getBasicTripTypes() : List<TripType> {
            return arrayListOf(BUS, FLIGHT, TRAIN)
        }
    }
}