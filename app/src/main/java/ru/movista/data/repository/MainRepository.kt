package ru.movista.data.repository

import ru.movista.data.source.local.KeyValueStorage
import ru.movista.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class MainRepository @Inject constructor(private val keyValueStorage: KeyValueStorage) {

    fun isAppFirstRun(): Boolean {
        return keyValueStorage.isAppFirstRun()
    }

    fun clearAppData() {
        keyValueStorage.remove(IntroRepository.TERMS_OF_USE_ACCEPTED_KEY)
        keyValueStorage.remove(IntroRepository.PRIVACY_POLICY_ACCEPTED_KEY)
        keyValueStorage.remove(IntroRepository.PERSONAL_DATA_POLICY_ACCEPTED_KEY)
        keyValueStorage.remove(IntroRepository.STARTING_INTRO_SEEN_KEY)
        keyValueStorage.remove(ProfileRepository.USER_IS_AUTHORIZED)
        keyValueStorage.remove(ProfileRepository.USER_IS_REGISTERED)
        keyValueStorage.remove(ProfileRepository.USER_LAST_NAME)
        keyValueStorage.remove(ProfileRepository.USER_NAME)
        keyValueStorage.removeAppFirstRun()
        keyValueStorage.clearToken()
        keyValueStorage.clearRefreshToken()
    }
}