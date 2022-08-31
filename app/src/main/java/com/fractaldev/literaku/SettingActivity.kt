package com.fractaldev.literaku

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import java.util.*
import kotlin.math.abs

class SettingActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var gestureDetector: GestureDetector

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var initialzedTTS: Boolean = false

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale("id", "ID")

                    val speedSpeech = Utils.getSettingsValue("SPEED_SPEECH", this)
                    if (speedSpeech != null) {
                        var speedSpeechInFloat = speedSpeech.toFloatOrNull()
                        if (speedSpeechInFloat == null) speedSpeechInFloat = 1F
                        textToSpeechEngine.setSpeechRate(speedSpeechInFloat)
                    }

                    initialzedTTS = true
                }
            })
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }
    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        gestureDetector = GestureDetector(this)

        supportFragmentManager.beginTransaction().add(R.id.settingHolder, MyPreferenceFragment()).commit()

        textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
            }

            override fun onDone(utteranceId: String?) {}

            override fun onError(utteranceId: String?) {}
        })

        firstTalkAfterOpen()
    }

    // Gesture Function Override
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        return
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    Utils.activateVoiceCommand(this@SettingActivity,
                        PenjelajahActivity.REQUEST_CODE_STT
                    )
                }
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PenjelajahActivity.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        if (Utils.executeVoiceCommand(this, recognizedText.lowercase())) {

                        }
                    }
                }
            }
        }
    }

    private fun firstTalkAfterOpen() {
        var text = "anda memasuki halaman pengaturan."
        speak(text)
    }

    //Speaks the text with TextToSpeech
    private fun speak(text: String) =
        if (initialzedTTS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "tts1")
            } else {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null)
            }
        else Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "tts1")
            } else {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null)
            }
        }, 1250)
}