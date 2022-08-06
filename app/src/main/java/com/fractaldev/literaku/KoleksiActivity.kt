package com.fractaldev.literaku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.fractaldev.literaku.databinding.ActivityKoleksiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KoleksiActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityKoleksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityKoleksiBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        setToolbar()

        fetchBooks()
    }

    fun setToolbar() {
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@KoleksiActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }

    fun fetchBooks() {
        showLoading(true)

        val client = ApiConfig.getApiService().getBooks()
        client.enqueue(object : Callback<List<KoleksiResponseItem>> {
            override fun onResponse(
                call: Call<List<KoleksiResponseItem>>,
                response: Response<List<KoleksiResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) setListBooks(responseBody)
                } else {
                    Log.e("KoleksiActivity", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<KoleksiResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e("KoleksiActivity", "onFailure: ${t.message}")
            }
        })
    }

    private fun setListBooks(books: List<KoleksiResponseItem>) {
        val listBooks = ArrayList<Buku>()

        for (book in books) {
            listBooks.add(
                Buku(
                    title = book.title,
                    author = book.author,
                    bookUrl = book.url,
                    coverURL = book.cover
                )
            )
        }

        activityBinding.rvKoleksi.layoutManager = LinearLayoutManager(this)
        val adapter = ListKoleksiAdapter(listBooks)
        activityBinding.rvKoleksi.adapter = adapter

        adapter.setOnItemClickCallback(object : ListKoleksiAdapter.OnItemClickCallback {
            override fun onItemClicked(buku: Buku) {
                showSelectedBuku(buku)
            }
        })
    }

    private fun showSelectedBuku(buku: Buku) {
        val moveIntent = Intent(this@KoleksiActivity, BukuActivity::class.java)
        moveIntent.putExtra("ViewType", "internet")
        moveIntent.putExtra("SelectedBook", ""+buku.bookUrl)
        moveIntent.putExtra("LastPageRead", 0)
        startActivity(moveIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) activityBinding.progressBar.visibility = View.VISIBLE
        else activityBinding.progressBar.visibility = View.GONE
    }
}