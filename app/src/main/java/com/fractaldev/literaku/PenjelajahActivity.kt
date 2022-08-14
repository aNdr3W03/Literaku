package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.WebView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import com.fractaldev.literaku.databinding.ActivityPenjelajahBinding
import kotlin.math.abs

class PenjelajahActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityPenjelajahBinding
    private lateinit var gestureDetector: GestureDetector

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    companion object {
        internal const val REQUEST_CODE_STT = 1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityPenjelajahBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setWebView(activityBinding.elWebView)
        setSearchField(activityBinding.penjelajahSearchField)

        gestureDetector = GestureDetector(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setWebView(element: WebView) {
        element.settings.loadsImagesAutomatically = true
        element.settings.javaScriptEnabled = true
        element.settings.domStorageEnabled = true

        element.settings.setSupportZoom(true)
        element.settings.builtInZoomControls = true
        element.settings.displayZoomControls = false

        element.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        element.webViewClient = LtkWebViewClient(this@PenjelajahActivity)

        element.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@PenjelajahActivity,
                    REQUEST_CODE_STT
                )
            }
            override fun onSwipeRight() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@PenjelajahActivity,
                    REQUEST_CODE_STT
                )
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setSearchField(element: EditText) {
        element.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= element.right - element.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    search(element.text.toString())
                    return@OnTouchListener true
                }
            }
            false
        })

        element.setOnKeyListener { _, keyCode, keyevent ->
            if (keyevent.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                search(element.text.toString())
                true
            } else false
        }

    }

    private fun search(query: String) {
        var sendQuery = ""
        if (query != "" && query != null) sendQuery = "search?q=filetype%3Apdf+$query"

        activityBinding.elWebView.loadUrl("https://www.google.com/$sendQuery")
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
                    Utils.activateVoiceCommand(this@PenjelajahActivity,
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