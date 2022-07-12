package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()

        // Onclick Button Set
        var btnPenjelajah = findViewById<Button> (R.id.btnPenjelajah)
        btnPenjelajah.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, PenjelajahActivity::class.java)
            startActivity(moveIntent)
        }
        var btnRiwayat = findViewById<Button> (R.id.btnRiwayat)
        btnRiwayat.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, RiwayatActivity::class.java)
            startActivity(moveIntent)
        }
        var btnKoleksi = findViewById<Button> (R.id.btnKoleksi)
        btnKoleksi.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, KoleksiActivity::class.java)
            startActivity(moveIntent)
        }
        var btnPanduan = findViewById<Button> (R.id.btnPanduan)
        btnPanduan.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, PanduanActivity::class.java)
            startActivity(moveIntent)
        }
    }

    fun setToolbar() {
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }
}