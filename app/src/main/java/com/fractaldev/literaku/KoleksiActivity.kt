package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KoleksiActivity : AppCompatActivity() {
    private lateinit var rvKoleksi: RecyclerView
    private val list = ArrayList<Buku>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_koleksi)
        setToolbar()

        rvKoleksi = findViewById(R.id.rvKoleksi)
        rvKoleksi.setHasFixedSize(true)

        list.addAll(listBuku)
        showRecyclerList()
    }

    fun setToolbar() {
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@KoleksiActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private val listBuku: ArrayList<Buku>
        get() {
            // DATA DUMMY
            // Add Title
            val dataTitle = ArrayList<String>()
            dataTitle.add("Pembaharuan strategi pendidikan")
            dataTitle.add("Bicara Itu Ada Seninya")
            dataTitle.add("Mukjizat Keterbatasan")
            dataTitle.add("Melihat Dunia Tanpa Mata")

            // Add Author
            val dataAuthor = ArrayList<String>()
            dataAuthor.add("Prof. Dr. Achmad Sanusi")
            dataAuthor.add("Oh Su Hyang")
            dataAuthor.add("Jihad Al-Maliki")
            dataAuthor.add("Poppy Diah")

            val listBuku = ArrayList<Buku>()
            for (i in dataTitle.indices) {
                val hero = Buku(dataTitle[i], "Desc", dataAuthor[i])
                listBuku.add(hero)
            }
            return listBuku
        }
    private fun showRecyclerList() {
        rvKoleksi.layoutManager = LinearLayoutManager(this)
        val listKoleksiAdapter = ListKoleksiAdapter(list)
        rvKoleksi.adapter = listKoleksiAdapter
    }
}