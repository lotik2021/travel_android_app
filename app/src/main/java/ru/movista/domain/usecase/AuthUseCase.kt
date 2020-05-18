package ru.movista.domain.usecase

import io.reactivex.Observable
import io.reactivex.Single
import ru.movista.data.entity.PhoneAuthResponse
import ru.movista.data.entity.RegenerateCodeResponse
import ru.movista.data.entity.RegistrateUserResponse
import ru.movista.data.entity.VerifyPhoneResponse
import ru.movista.data.repository.FullAuthRepository
import ru.movista.data.repository.ProfileRepository
import ru.movista.di.FlowScope
import ru.movista.domain.model.User
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface IAuthUseCase

interface AuthWithPhoneUseCase {
    fun authWithPhone(phoneNumber: String, recaptchaToken: String): Single<PhoneAuthResponse>
}

interface VerifyPhoneUseCase {
    fun getExpiresIn(): Int
    fun getExpiresInCounter(): Observable<Long>

    fun verifyPhone(code: String): Single<VerifyPhoneResponse>

    fun regenerateCode(): Single<RegenerateCodeResponse>

    fun setUserAuthorized()
    fun setUserRegistered()
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
}

interface RegisterUserUseCase {
    fun registerUser(user: User): Single<RegistrateUserResponse>
    fun setUserRegistered()
}

@FlowScope
class AuthUseCase @Inject constructor(
    private val fullAuthRepository: FullAuthRepository, private val profileRepository: ProfileRepository
) : AuthWithPhoneUseCase, VerifyPhoneUseCase, RegisterUserUseCase, IAuthUseCase {

    private var expiresIn: Int = 0

    private var phoneNumber: String = ""
    private var regenerateCodeUid: String = ""

    private var requestId: String = ""

    override fun authWithPhone(phoneNumber: String, recaptchaToken: String): Single<PhoneAuthResponse> {
        val phoneWithPrefix = "+7$phoneNumber"

        return fullAuthRepository.authWithPhone(phoneWithPrefix, recaptchaToken)
            .doOnSuccess { phoneAuthResponse ->
                phoneAuthResponse.error?.let { throw (it) }

                this.regenerateCodeUid = phoneAuthResponse.uid ?: ""
                this.expiresIn = phoneAuthResponse.refresh_after
                this.phoneNumber = phoneWithPrefix
            }
    }

    override fun verifyPhone(code: String): Single<VerifyPhoneResponse> {
        return fullAuthRepository.verifyWithCode(this.phoneNumber, code)
            .doOnSuccess {
                if (it.error != null) throw it.error

                this.requestId = it.request_id
            }
    }

    override fun getExpiresIn() = expiresIn

    override fun getExpiresInCounter(): Observable<Long> {
        // + 1 чтобы показать последнее значение (1)
        return Observable.intervalRange(0, expiresIn.toLong() + 1, 0, 1, TimeUnit.SECONDS)
            .map { expiresIn - it }
    }

    override fun registerUser(user: User): Single<RegistrateUserResponse> {
        return fullAuthRepository.registerUser(requestId, user)
            .doOnSuccess { registerUserResponse ->
                registerUserResponse.error?.let { throw (it) }

                saveAccessToken(registerUserResponse.access_token)
                saveRefreshToken(registerUserResponse.refresh_token)
                this.requestId = registerUserResponse.request_id
            }
    }

    override fun regenerateCode(): Single<RegenerateCodeResponse> {
        return fullAuthRepository.regenerateCode(regenerateCodeUid)
            .doOnSuccess { regenerateCodeResponse ->
                regenerateCodeResponse.error?.let { throw (it) }

                this.expiresIn = regenerateCodeResponse.refresh_after
                this.regenerateCodeUid = regenerateCodeResponse.uid ?: ""
            }
    }

    override fun setUserAuthorized() {
        profileRepository.setUserIsAuthorized(true)
    }

    override fun setUserRegistered() {
        profileRepository.setUserIsRegistered(true)
    }

    override fun saveAccessToken(token: String) {
        fullAuthRepository.setAccessToken(token)
    }

    override fun saveRefreshToken(token: String) {
        fullAuthRepository.saveRefreshToken(token)
    }
}
