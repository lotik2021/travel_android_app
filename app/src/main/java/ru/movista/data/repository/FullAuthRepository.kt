package ru.movista.data.repository

import io.reactivex.Single
import ru.movista.data.entity.*
import ru.movista.data.source.local.KeyValueStorage
import ru.movista.data.source.remote.Api
import ru.movista.di.FlowScope
import ru.movista.domain.model.User
import javax.inject.Inject

@FlowScope
class FullAuthRepository @Inject constructor(
    private val api: Api,
    private val keyValueStorage: KeyValueStorage
) {

    fun authWithPhone(phoneNumber: String, recaptchaToken: String): Single<PhoneAuthResponse> {
        return api.authorizeWithPhoneNumber(
            keyValueStorage.getToken(),
            PhoneAuthRequest(phoneNumber, recaptchaToken)
        )
    }

    fun verifyWithCode(vuid: String, code: String): Single<VerifyPhoneResponse> {
        return api.verifyPhoneNumber(
            keyValueStorage.getToken(),
            VerifyPhoneRequest(vuid, code)
        )
    }

    fun registerUser(requestId: String, user: User): Single<RegistrateUserResponse> {
        return api.registerUser(
            keyValueStorage.getToken(),
            RegistrateUserRequest(requestId.toLowerCase(), user.firstName, user.lastName, user.email)
        )
    }

    fun regenerateCode(vuid: String): Single<RegenerateCodeResponse> {
        return api.regenerateCode(
            keyValueStorage.getToken(),
            RegenerateCodeRequest(vuid)
        )
    }

    fun setAccessToken(token: String) {
        keyValueStorage.saveToken(token)
    }

    fun saveRefreshToken(token: String) {
        keyValueStorage.saveRefreshToken(token)
    }
}