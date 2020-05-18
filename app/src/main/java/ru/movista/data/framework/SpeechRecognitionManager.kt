package ru.movista.data.framework

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class SpeechRecognitionManager(val context: Context) : RecognitionListener {
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val recognitionResultsSubject = PublishSubject.create<Notification<SpeechRecognitionResult>>()

    init {
        recognizer.setRecognitionListener(this)
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Timber.tag("Speech").i("onReadyForSpeech $params")
        recognitionResultsSubject.onNext(Notification.createOnNext(SpeechRecognitionStart))
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onPartialResults(partialResults: Bundle?) {
        val recognizedResults = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        Timber.tag("Speech").i("onPartialResults $recognizedResults")

        if (!recognizedResults.isNullOrEmpty()) {
            Timber.d("SpeechRecognitionManager partialResults: $recognizedResults")
            // первый элемент массива содержит самый точный результат
            recognitionResultsSubject.onNext(Notification.createOnNext(SpeechRecognitionData(recognizedResults[0])))
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onBeginningOfSpeech() {}

    override fun onEndOfSpeech() {
        Timber.tag("Speech").i("onEndOfSpeech")
        recognitionResultsSubject.onNext(Notification.createOnNext(SpeechRecognitionEnd))
    }

    override fun onError(error: Int) {
        Timber.tag("Speech").i("onError $error")

        Timber.d("SpeechRecognitionManager onError code: $error")
        recognitionResultsSubject.onNext(Notification.createOnError(Throwable(error.toString())))
    }

    override fun onResults(results: Bundle?) {
        val recognizedResults = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        Timber.tag("Speech").i("onResults $recognizedResults")

        if (!recognizedResults.isNullOrEmpty()) {
            Timber.d("SpeechRecognitionManager onResults: $recognizedResults")
            // первый элемент массива содержит самый точный результат
            recognitionResultsSubject.onNext(Notification.createOnNext(SpeechRecognitionData(recognizedResults[0])))
        }
    }

    fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun observeRecognitionResults(): Observable<Notification<SpeechRecognitionResult>> = recognitionResultsSubject

    fun startRecognize() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtras(
                bundleOf(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS to true,
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL to RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                    // не работает, ну и фиг с ним
                    // https://stackoverflow.com/questions/34342706/android-recognizerintent-extra-max-results-not-limiting-number-of-results
                    RecognizerIntent.EXTRA_MAX_RESULTS to 1
                )
            )
        }

        recognizer.startListening(intent)
    }

    fun stopRecognize() {
        recognizer.stopListening()
        recognizer.cancel()
        recognitionResultsSubject.onNext(Notification.createOnNext(SpeechRecognitionEnd))
    }
}

sealed class SpeechRecognitionResult

data class SpeechRecognitionData(val result: String) : SpeechRecognitionResult()

object SpeechRecognitionStart : SpeechRecognitionResult()

object SpeechRecognitionEnd : SpeechRecognitionResult()