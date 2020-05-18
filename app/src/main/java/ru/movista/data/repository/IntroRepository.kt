package ru.movista.data.repository

import ru.movista.data.source.local.KeyValueStorage
import ru.movista.di.FragmentScope
import javax.inject.Inject

@FragmentScope
class IntroRepository @Inject constructor(private val keyValueStorage: KeyValueStorage) {
    companion object {
        const val TERMS_OF_USE_ACCEPTED_KEY = "TERMS_OF_USE_ACCEPTED"
        const val PRIVACY_POLICY_ACCEPTED_KEY = "PRIVACY_POLICY_ACCEPTED"
        const val PERSONAL_DATA_POLICY_ACCEPTED_KEY = "PERSONAL_DATA_POLICY_ACCEPTED"
        const val STARTING_INTRO_SEEN_KEY = "STARTING_INTRO_SEEN"
    }

    fun setTermsOfUseAccepted() {
        keyValueStorage.putBoolean(TERMS_OF_USE_ACCEPTED_KEY, true)
    }

    fun setPrivacyPolicyAccepted() {
        keyValueStorage.putBoolean(PRIVACY_POLICY_ACCEPTED_KEY, true)
    }

    fun setPersonalDataPolicyAccepted() {
        keyValueStorage.putBoolean(PERSONAL_DATA_POLICY_ACCEPTED_KEY, true)
    }

    fun setStartingIntroSeen() {
        keyValueStorage.putBoolean(STARTING_INTRO_SEEN_KEY, true)
    }

    fun isTermsOfUseAccepted() = keyValueStorage.getBoolean(TERMS_OF_USE_ACCEPTED_KEY)

    fun isPrivacyPolicyAccepted() = keyValueStorage.getBoolean(PRIVACY_POLICY_ACCEPTED_KEY)

    fun isPersonalDataPolicyAccepted() = keyValueStorage.getBoolean(PERSONAL_DATA_POLICY_ACCEPTED_KEY)

    fun isStartingIntroSeen(): Boolean = keyValueStorage.getBoolean(STARTING_INTRO_SEEN_KEY)

    fun setAppFirstRun() {
        keyValueStorage.setAppFirstRun()
    }
}