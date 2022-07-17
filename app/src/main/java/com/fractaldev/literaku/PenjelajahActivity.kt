package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class PenjelajahActivity : AppCompatActivity() {
    lateinit var penjelajahSearchField: EditText
    lateinit var elWebView: WebView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penjelajah)

        elWebView = findViewById(R.id.elWebView)
        setWebView(elWebView)

        penjelajahSearchField = findViewById(R.id.penjelajahSearchField)
        setSearchField(penjelajahSearchField)
    }

    fun setWebView(element: WebView) {
        element.getSettings().setLoadsImagesAutomatically(true);
        element.getSettings().setJavaScriptEnabled(true);
        element.getSettings().setDomStorageEnabled(true);

        element.getSettings().setSupportZoom(true);
        element.getSettings().setBuiltInZoomControls(true);
        element.getSettings().setDisplayZoomControls(false);

        element.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        element.setWebViewClient(WebViewClient())
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setSearchField(element: EditText) {
        element.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= element.getRight() - element.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    search(element.getText().toString())
                    return@OnTouchListener true
                }
            }
            false
        })

        element.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View?, keyCode: Int, keyevent: KeyEvent): Boolean {
                return if (keyevent.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    search(element.getText().toString())
                    true
                } else false
            }
        })

    }

    fun search(query: String) {
        var sendQuery = ""
        if (query != "" && query != null) sendQuery = "search?q=$query"

        elWebView.loadUrl("https://www.google.com/$sendQuery")
    }
}