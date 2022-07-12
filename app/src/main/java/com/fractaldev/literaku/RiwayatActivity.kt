package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RiwayatActivity : AppCompatActivity() {
    private lateinit var rvRiwayat: RecyclerView
    private val list = ArrayList<Buku>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        setToolbar()

        rvRiwayat = findViewById(R.id.rvRiwayat)
        rvRiwayat.setHasFixedSize(true)

        list.addAll(listBuku)
        showRecyclerList()
    }

    fun setToolbar() {
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@RiwayatActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private val listBuku: ArrayList<Buku>
        get() {
            // DATA DUMMY
            // Add Title
            val dataTitle = ArrayList<String>()
            dataTitle.add("1. The Covid-19 Epidemic")
            dataTitle.add("2. Rumah Baca Jendela...")
            dataTitle.add("3. Membangun Budaya L...")
            dataTitle.add("4. Strategi Peningkatan ...")

            val listBuku = ArrayList<Buku>()
            for (i in dataTitle.indices) {
                val hero = Buku(dataTitle[i], "Desc")
                listBuku.add(hero)
            }
            return listBuku
        }
    private fun showRecyclerList() {
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        val listRiwayatAdapter = ListRiwayatAdapter(list)
        rvRiwayat.adapter = listRiwayatAdapter
    }
}