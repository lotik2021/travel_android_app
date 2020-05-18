package ru.movista.domain.model

data class User(
    val firstName: String,
    val lastName: String? = null,
    val email: String
)

//data class UserProfile(
//    val firstName: String,
//    val lastName: String? = null,
//    val transportTypes: List<ProfileTransportType>
//)