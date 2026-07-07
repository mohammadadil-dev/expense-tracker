package com.expensetracker.app.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Thin wrapper around Android's on-device [SpeechRecognizer].
 *
 * Usage:
 *   val mgr = VoiceInputManager(context)
 *   mgr.start(locale) { result -> ... }
 *   // or
 *   mgr.start(locale, onResult = { ... }, onError = { ... })
 *   mgr.destroy()   // call from onDispose / onStop
 */
class VoiceInputManager(private val context: Context) {

    sealed class State {
        object Idle       : State()
        object Listening  : State()
        data class Done(val text: String)    : State()
        data class Error(val code: Int)      : State()
    }

    private var recognizer: SpeechRecognizer? = null

    /** True when the device has an on-device speech recognition service. */
    fun isAvailable(): Boolean =
        SpeechRecognizer.isRecognitionAvailable(context)

    /**
     * Start listening. Calls [onResult] with the top hypothesis, or [onError]
     * with the SpeechRecognizer error code.
     */
    fun start(
        locale: Locale = Locale.getDefault(),
        onResult: (String) -> Unit,
        onError:  (Int)    -> Unit = {}
    ) {
        stop()                          // clean up any previous session
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).also { sr ->
            sr.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}

                override fun onResults(results: Bundle?) {
                    val matches = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val top = matches?.firstOrNull() ?: ""
                    onResult(top)
                }

                override fun onError(error: Int) {
                    onError(error)
                }
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
                // Also accept default locale as fallback
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, locale.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                // Reduce silence timeout for snappier UX
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
            }
            sr.startListening(intent)
        }
    }

    /** Stop the current recognition session without destroying the object. */
    fun stop() {
        recognizer?.stopListening()
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
    }

    /** Must be called when the composable / Activity is destroyed. */
    fun destroy() = stop()

    companion object {
        /** Human-readable label for a SpeechRecognizer error code (for debugging). */
        fun errorLabel(code: Int): String = when (code) {
            SpeechRecognizer.ERROR_AUDIO                -> "Audio error"
            SpeechRecognizer.ERROR_CLIENT               -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "No RECORD_AUDIO permission"
            SpeechRecognizer.ERROR_NETWORK              -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT      -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH             -> "No speech detected"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY      -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER               -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT       -> "Speech timeout"
            else                                        -> "Unknown error ($code)"
        }
    }
}
