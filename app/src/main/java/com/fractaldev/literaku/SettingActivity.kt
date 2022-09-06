package com.fractaldev.literaku

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import com.fractaldev.literaku.databinding.ActivitySettingBinding
import kotlin.math.abs

class SettingActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivitySettingBinding
    private lateinit var gestureDetector: GestureDetector
    private var helpers = Helpers(this)

    lateinit var mDialog: Dialog

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var textBantuan: String = ""

    override fun onPause() {
        helpers.textToSpeechEngine.stop()
        super.onPause()
    }
    override fun onDestroy() {
        helpers.textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)
        supportFragmentManager.beginTransaction().add(R.id.settingHolder, MyPreferenceFragment()).commit()

        setToolbar()
        getResourceBantuan()

        helpers.textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
            }

            override fun onDone(utteranceId: String?) {
                mDialog.dismiss()
            }

            override fun onError(utteranceId: String?) {}
        })

        firstTalkAfterOpen()
    }

    private fun setToolbar() {
        activityBinding.includeSetting2.fabBantuan.setOnClickListener {
            openBantuan()
        }
    }

    private fun getResourceBantuan() {
        var arrText: MutableList<String> = mutableListOf()
        arrText.add(resources.getString(R.string.bantuanPengaturan0))
        arrText.add(resources.getString(R.string.bantuanPengaturan1))
        arrText.add(resources.getString(R.string.bantuanPengaturan2))
        arrText.add(resources.getString(R.string.bantuanPengaturan3))
        arrText.add(resources.getString(R.string.bantuanPengaturan4))

        textBantuan = arrText.joinToString(" ")
    }

    private fun openBantuan() {
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.bantuan_setting)
        mDialog.show()

        mDialog.setOnDismissListener {
            helpers.textToSpeechEngine.stop()
        }

        helpers.speak(textBantuan)
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
                    helpers.activateVoiceCommand()
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
            helpers.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        if (helpers.executeVoiceCommand(recognizedText.lowercase())) {
                            var command = recognizedText.lowercase()
                            var arrCommand = command.split(" ").toMutableList()

                            if (Commands.openBantuan.contains(command)) {
                                openBantuan()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun firstTalkAfterOpen() {
        var text = "anda memasuki halaman pengaturan."
        helpers.speak(text)
    }
}