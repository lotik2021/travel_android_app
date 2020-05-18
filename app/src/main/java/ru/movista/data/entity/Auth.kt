package ru.movista.data.entity

data class PhoneAuthRequest(
    val phone: String, // в формате +79166476865
    val recaptcha_token: String // в формате +79166476865
)

data class PhoneAuthResponse(
    val refresh_after: Int,
    val uid: String?,

    val error: ApiError? = null
)

data class VerifyPhoneRequest(
    val vuid: String,
    val code: String, // test 0000

    val error: ApiError? = null
)

data class RefreshTokenRequest(
    val refresh_token: String,
    val user_id: String
)

data class VerifyPhoneResponse(
    val access_token: String,
    val refresh_token: String = "",
    val request_id: String,
    val has_email: Boolean,
    val user_id: String,

    val error: ApiError? = null
)

data class RegistrateUserRequest(
    val request_id: String,
    val name: String,
    val last_name: String? = null,
    val email: String // в lowercase
)

data class RegistrateUserResponse(
    val access_token: String,
    val refresh_token: String,
    val request_id: String,
    val has_email: Boolean,

    val error: ApiError? = null
)

data class RegenerateCodeRequest(
    val uid: String
)

data class RegenerateCodeResponse(
    val refresh_after: Int,
    val uid: String?,

    val error: ApiError? = null
)