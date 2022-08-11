package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class PanduanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panduan)
        setToolbar()

    }

    fun setToolbar() {
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@PanduanActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }
}