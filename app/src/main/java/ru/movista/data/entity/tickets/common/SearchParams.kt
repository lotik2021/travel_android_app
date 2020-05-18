package ru.movista.data.entity.tickets.common

open class SearchParams {
    var searchId: Long? = null
    var currencyCode: String = ""
    var cultureCode: String = ""
    var departureBegin: String = ""
    var returnDepartureBegin: String? = null
    var from: Long = 0
    var to: Long = 0
    var customers: List<Customer> = emptyList()
    var tripTypes: List<String> = emptyList()
    var airServiceClass: String = ""
}

class Customer(
    val age: Int = 0,
    val seatRequired: Boolean = false
)