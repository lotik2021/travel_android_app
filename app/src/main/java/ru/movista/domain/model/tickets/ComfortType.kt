package ru.movista.domain.model.tickets

import ru.movista.R
import java.io.Serializable

enum class ComfortType(val value: String, val resId: Int) : Serializable {
    ECONOMY("economy", R.string.comfort_type_economy),
    PREMIUM_ECONOMY("premiumEconomy", R.string.comfort_type_premium_economy),
    BUSINESS("business", R.string.comfort_type_business),
    FIRST_CLASS("first", R.string.comfort_type_first_class);

    companion object {
        fun getById(id: String?): ComfortType? {
            values().forEach {
                if (it.value == id) {
                    return it
                }
            }
            return null
        }
    }
}