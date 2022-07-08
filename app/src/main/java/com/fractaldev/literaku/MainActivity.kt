package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Onclick Button Set
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
}