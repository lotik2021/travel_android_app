package ru.movista.domain.usecase

import io.reactivex.Observable
import io.reactivex.Single
import ru.movista.data.entity.ApiError
import ru.movista.data.entity.DialogResponse
import ru.movista.data.entity.SessionEntity
import ru.movista.data.entity.tickets.AsyncAvailableResponse
import ru.movista.data.repository.ActionsRepository
import ru.movista.data.repository.AuthRepository
import ru.movista.di.FragmentScope
import ru.movista.domain.model.UserData
import ru.movista.utils.reportNullFieldError
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FragmentScope
class ActionsUseCase @Inject constructor(
    private val actionsRepository: ActionsRepository,
    private val authRepository: AuthRepository
) {
    private var currentSessionData: SessionEntity? = null

    fun createSession(): Single<DialogResponse> {
        return if (!authRepository.isTokenExists()) {
            // возвращаем только результат создания сессии
            authRepository.authorize()
                .flatMap { actionsRepository.createSession() }
                .doOnSuccess { dialogResponse ->
                    throwAnErrorOrSaveSessionData(dialogResponse)
                }
        } else {
            actionsRepository.createSession()
                .doOnSuccess { throwAnErrorOrSaveSessionData(it) }
        }
    }

    fun asyncAvailable(): Observable<AsyncAvailableResponse> {
        return Observable.interval(0, 1, TimeUnit.MINUTES)
            .flatMapSingle { actionsRepository.asyncAvailable() }
            .repeat()
    }

    fun sendChosenAction(
        chosenActionId: String,
        userData: UserData? = null
    ): Single<DialogResponse> {

        currentSessionData?.let { sessionEntity ->
            return when {
                userData != null -> {
                    actionsRepository
                        .sendChosenActionWithUserData(chosenActionId, sessionEntity, userData)
                        .doOnSuccess { throwAnErrorOrSaveSessionData(it) }
                }
                else -> {
                    actionsRepository
                        .sendChosenAction(chosenActionId, sessionEntity)
                        .doOnSuccess { throwAnErrorOrSaveSessionData(it) }
                }
            }
        } ?: run {
            reportNullFieldError("currentSessionData")
            return Single.error(IllegalStateException())
        }
    }

    fun sendEnteredUserMessage(userMessage: String): Single<DialogResponse> {
        currentSessionData?.let { sessionEntity ->
            return actionsRepository
                .sendEnteredUserMessage(sessionEntity, userMessage)
                .doOnSuccess { throwAnErrorOrSaveSessionData(it) }
        } ?: kotlin.run {
            reportNullFieldError("currentSessionData")
            return Single.error(IllegalStateException())
        }
    }

    private fun throwAnErrorOrSaveSessionData(dialogResponse: DialogResponse) {
        if (dialogResponse.error != null) throw ApiError(dialogResponse.error.code, dialogResponse.error.message)

        dialogResponse.session?.let {
            currentSessionData = it
        } ?: reportNullFieldError("response.session")
    }

}