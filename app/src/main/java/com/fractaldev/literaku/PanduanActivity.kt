package com.fractaldev.literaku

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton

import com.fractaldev.literaku.databinding.ActivityPanduanBinding
import kotlin.math.abs

class PanduanActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityPanduanBinding
    private lateinit var gestureDetector: GestureDetector

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityPanduanBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)

        setToolbar()
    }

    private fun setToolbar() {
        activityBinding.backToolbar.backBtn.setOnClickListener {
            finish()
        }
        activityBinding.backToolbar.settingBtn.setOnClickListener {
            val moveIntent = Intent(this@PanduanActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
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
                    Utils.activateVoiceCommand(this@PanduanActivity,
                        REQUEST_CODE_STT
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
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        Utils.executeVoiceCommand(this, recognizedText.lowercase())
                    }
                }
            }
        }
    }
}