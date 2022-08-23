package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.fractaldev.literaku.databinding.ActivityKoleksiBinding
import kotlin.math.abs

class KoleksiActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityKoleksiBinding
    private lateinit var gestureDetector: GestureDetector

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var listBooks = ArrayList<Buku>()

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityKoleksiBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)

        setToolbar()
        fetchBooks()
    }

    fun setToolbar() {
        activityBinding.includeKoleksi1.settingBtn.setOnClickListener {
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setListBooks(books: List<KoleksiResponseItem>) {
        listBooks = ArrayList<Buku>()

        for (book in books) {
            listBooks.add(
                Buku(
                    uuid = book.id.toString(),
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

        activityBinding.rvKoleksi.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@KoleksiActivity,
                    PenjelajahActivity.REQUEST_CODE_STT
                )
            }
            override fun onSwipeRight() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@KoleksiActivity,
                    PenjelajahActivity.REQUEST_CODE_STT
                )
            }
        })
    }

    private fun showSelectedBuku(buku: Buku) {
        val moveIntent = Intent(this@KoleksiActivity, BukuActivity::class.java)
        moveIntent.putExtra("SelectedBookID", ""+buku.uuid)
        moveIntent.putExtra("SelectedBookUrl", ""+buku.bookUrl)
        moveIntent.putExtra("SelectedBookLastPage", 0)
        startActivity(moveIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) activityBinding.progressBar.visibility = View.VISIBLE
        else activityBinding.progressBar.visibility = View.GONE
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
                    Utils.activateVoiceCommand(this@KoleksiActivity, REQUEST_CODE_STT)
                }
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }

    // Commands - Override from Utils Commands
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]

                        if (Utils.executeVoiceCommand(this, recognizedText.lowercase())) {
                            // Select Book by title
                            var command = recognizedText.lowercase()
                            var arrCommand = command.split(" ").toMutableList()

                            if (
                                arrCommand[0] == "pilih" ||
                                arrCommand[0] == "memilih" ||
                                arrCommand[0] == "baca" ||
                                arrCommand[0] == "membaca" ||
                                arrCommand[0] == "buka" ||
                                arrCommand[0] == "membuka" ||
                                // bug
                                arrCommand[0] == "bukabuku" ||
                                arrCommand[0] == "bacabuku" ||
                                arrCommand[0] == "pilihbuku"
                            ) {
                                arrCommand.removeAt(0)

                                // remove word "buku"
                                if (arrCommand[0] == "buku") arrCommand.removeAt(0)

                                val title = arrCommand.joinToString(" ")
                                val titleToSearch = arrCommand.joinToString("")

                                if (titleToSearch != "") {
                                    val listBooks = listBooks
                                    val selectedBook = listBooks.find { it ->
                                        it.title
                                            .toLowerCase()
                                            .replace("[^A-Za-z0-9 ]".toRegex(),"")
                                            .replace("\\s+".toRegex(), "")
                                            .contains(
                                                titleToSearch
                                                    .replace("[^A-Za-z0-9 ]".toRegex(),"")
                                                    .replace("\\s+".toRegex(), "")
                                                ) ||
                                        titleToSearch
                                            .replace("[^A-Za-z0-9 ]".toRegex(),"")
                                            .replace("\\s+".toRegex(), "")
                                            .contains(
                                                it.title
                                                    .toLowerCase()
                                                    .replace("[^A-Za-z0-9 ]".toRegex(),"")
                                                    .replace("\\s+".toRegex(), "")
                                            )
                                    }
                                    if (selectedBook != null) {
                                        showSelectedBuku(selectedBook)
                                    } else {
                                        val textError = "Judul buku \"$title\" tidak ditemukan. Silahkan coba lagi."
                                        Toast.makeText(this, textError, Toast.LENGTH_LONG).show()
                                    }
                                }
                                else {
                                    val textError = "Judul buku belum disebutkan. Silahkan coba lagi."
                                    Toast.makeText(this, textError, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}