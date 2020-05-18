package ru.movista.presentation.tickets.passengers

import android.content.res.Resources
import ru.movista.R
import ru.movista.di.FragmentScope
import ru.movista.domain.model.tickets.AdultPassenger
import ru.movista.domain.model.tickets.PassengersInfo
import javax.inject.Inject

@FragmentScope
class PassengersConverter @Inject constructor(private val resources: Resources) {

    fun toPassengersInfoViewModel(passengersInfo: PassengersInfo): PassengersInfoViewModel {

        with(passengersInfo) {

            val adults = passengers.filterIsInstance<AdultPassenger>()
            val allChildren = passengers.filterNot { it is AdultPassenger }

            val childrenInfo = ArrayList<ChildInfoViewModel>()

            allChildren.forEach {
                val child = it

                val age = child.age

                val ageLabel = if (age == 0) {
                    resources.getString(R.string.passenger_child_age_zero)
                } else {
                    resources.getQuantityString(R.plurals.passengers_child_age, age, age)
                }
                val isSeatRequired = child.isSeatRequired

                val seatLabel = if (isSeatRequired) {
                    resources.getString(R.string.passenger_with_seat).toUpperCase()
                } else {
                    resources.getString(R.string.passenger_without_seat).toUpperCase()
                }

                childrenInfo.add(
                    ChildInfoViewModel(
                        child.age,
                        child.isSeatRequired,
                        ageLabel,
                        seatLabel
                    )
                )
            }

            return PassengersInfoViewModel(
                adults.size,
                childrenInfo
            )
        }
    }

}