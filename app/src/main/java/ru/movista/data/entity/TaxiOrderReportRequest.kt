package ru.movista.data.entity

data class TaxiOrderReportRequest(
    val used_link: String,
    val id: String, // deeplink id
    val device_os: String = "android"
)