package ru.movista.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import ru.movista.BuildConfig
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.RefreshTokenRequest
import ru.movista.data.entity.RegistrateUserResponse
import ru.movista.data.source.local.DeviceInfo
import ru.movista.data.source.local.JSON_MEDIA_TYPE
import ru.movista.data.source.local.KeyValueStorage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationHelper @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    private val deviceInfo: DeviceInfo
) {
    companion object {
        private const val AUTH_HEADER = "Authorization"
        private val JSON_TYPE = JSON_MEDIA_TYPE.toMediaTypeOrNull()
    }

    private val authErrorSubject = PublishSubject.create<Unit>()
    private val moshi = Moshi.Builder().build()

    fun getAuthErrorObservable(): Observable<Unit> = authErrorSubject

    inner class AuthInterceptor : Authenticator {

        @Synchronized
        override fun authenticate(route: Route?, originalResponse: Response): Request? {
            Timber.tag("auth").i("Authentication failed: 401")
            Timber.tag("auth").i("Try to perform shadow authorization")

            getRefreshTokenCall().execute().use { reAuthResponse ->
                if (reAuthResponse.isSuccessful) {
                    val response = parseAuthResponse(reAuthResponse.body?.string())
                    response?.let {
                        if (it.error != null) {
                            Timber.tag("auth").i("Shadow authorization attempt failed")
                            authErrorSubject.onNext(Unit)
                            throw it.error
                        } else {
                            keyValueStorage.saveRefreshToken(it.refresh_token)
                            keyValueStorage.saveToken(it.access_token)
                            Timber.tag("auth").i("Successful shadow authorization")
                        }
                    }

                    return originalResponse.request
                        .newBuilder()
                        .header(AUTH_HEADER, keyValueStorage.getToken())
                        .build()
                } else {
                    authErrorSubject.onNext(Unit)
                    throw ApiError("", "AuthError")
                }
            }
        }
    }

    private fun getRefreshTokenCall(): Call {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        val request = Request.Builder()
            .url(getUrl())
            .addHeader(AUTH_HEADER, keyValueStorage.getToken())
            .post(RequestBody.create(JSON_TYPE, createRefreshTokenRequest()))
            .build()

        return builder.build().newCall(request)
    }

    private fun parseAuthResponse(json: String?): RegistrateUserResponse? {
        if (json == null) {
            return null
        }

        val jsonAdapter: JsonAdapter<RegistrateUserResponse> = moshi.adapter(RegistrateUserResponse::class.java)
        return jsonAdapter.fromJson(json)
    }

    private fun createRefreshTokenRequest(): String {
        val request = RefreshTokenRequest(keyValueStorage.getRefreshToken(), deviceInfo.deviceId)
        val jsonAdapter = moshi.adapter(RefreshTokenRequest::class.java)
        return jsonAdapter.toJson(request)
    }

    private fun getUrl(): String = "${BuildConfig.API_BASE_URL}auth/refresh"
}