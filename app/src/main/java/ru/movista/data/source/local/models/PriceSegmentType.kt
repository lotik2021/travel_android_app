package ru.movista.data.source.local.models

enum class PriceSegmentType(val type: String) {
    ONE_WAY_PRICE("oneWayPrice"),
    ROUND_TRIP_PRICE("roundtripPrice"),
    ROUND_TRIP_PRICE_ALREADY_TAKEN("roundtripPriceAlreadyTaken"),
    ROUND_TRIP_PRICE_ALREADY_PARTIALLY_TAKEN("roundtripPriceAlreadyPartiallyTaken"),
    OTHER("");

    companion object {
        fun getById(id: String): PriceSegmentType {
            values().forEach {
                if (it.type == id) {
                    return it
                }
            }
            return OTHER
        }

        fun isTaken(priceType: PriceSegmentType?) : Boolean {
            return priceType in arrayListOf(ROUND_TRIP_PRICE_ALREADY_TAKEN, ROUND_TRIP_PRICE_ALREADY_PARTIALLY_TAKEN)
        }
    }
}