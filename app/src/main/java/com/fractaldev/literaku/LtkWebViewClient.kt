package com.fractaldev.literaku

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class LtkWebViewClient(private val context: Context) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        var regex = Regex("/\\.(pdf)\$/i")

        val moveIntent = Intent(context, BukuActivity::class.java)
        moveIntent.putExtra("ViewType", "internet")
        moveIntent.putExtra("SelectedBook", url)
        moveIntent.putExtra("LastPageRead", 0)
        context.startActivity(moveIntent)

        return true
    }
}