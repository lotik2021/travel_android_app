package ru.movista.domain.usecase

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.CompletableSubject
import ru.movista.data.repository.IntroRepository
import ru.movista.di.FragmentScope
import javax.inject.Inject

interface SlidePageUseCase {
    fun onAllTermsSelected()
    fun onSkipClick()
    fun onGeoAllowClick()
}

interface IntroUseCase {
    fun getAllTermsSelectedSubject(): Completable
    fun getSkipClickSubject(): Completable
    fun getGeoAllowClickSubject(): Observable<Unit>

    fun setTermsAccepted()
    fun isTermsAccepted(): Boolean
    fun isStartingIntroSeen(): Boolean
    fun setStartingIntroSeen()
    fun setApplicationFirstRun()
}

@FragmentScope
class IntroUseCaseImpl @Inject constructor(
    private val introRepository: IntroRepository
) : SlidePageUseCase, IntroUseCase {
    private val allTermsSelectedSubject: CompletableSubject = CompletableSubject.create()
    private val skipClickSubject: CompletableSubject = CompletableSubject.create()
    private val geoAllowClickSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    override fun onAllTermsSelected() = allTermsSelectedSubject.onComplete()
    override fun onSkipClick() = skipClickSubject.onComplete()
    override fun onGeoAllowClick() = geoAllowClickSubject.onNext(Unit)

    override fun getAllTermsSelectedSubject(): Completable = allTermsSelectedSubject
    override fun getSkipClickSubject(): Completable = skipClickSubject
    override fun getGeoAllowClickSubject(): Observable<Unit> = geoAllowClickSubject

    override fun setTermsAccepted() {
        introRepository.setPrivacyPolicyAccepted()
        introRepository.setPersonalDataPolicyAccepted()
        introRepository.setTermsOfUseAccepted()
    }

    override fun isTermsAccepted(): Boolean {
        return introRepository.isTermsOfUseAccepted()
                && introRepository.isPrivacyPolicyAccepted()
                && introRepository.isPersonalDataPolicyAccepted()
    }

    override fun isStartingIntroSeen(): Boolean {
        return introRepository.isStartingIntroSeen()
    }

    override fun setStartingIntroSeen() {
        introRepository.setStartingIntroSeen()
    }

    override fun setApplicationFirstRun() {
        introRepository.setAppFirstRun()
    }
}