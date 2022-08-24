package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.fractaldev.literaku.databinding.ActivityPenjelajahBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class PenjelajahActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityPenjelajahBinding
    private lateinit var gestureDetector: GestureDetector

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var itemsSearch = ArrayList<Penjelajah>()

    companion object {
        internal const val REQUEST_CODE_STT = 1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityPenjelajahBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

//        setWebView(activityBinding.elWebView)
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
//        if (query != "" && query != null) sendQuery = "filetype%3Apdf+$query"
//        activityBinding.elWebView.loadUrl("https://cse.google.com/cse?cx=f625daf1db8f54cd9&Q=$sendQuery")

        showLoading(true)

        if (query != "" && query != null) sendQuery = "filetype:pdf $query"
        val client = ApiConfig.getApiServiceCSE().getPenjelajahBooks(
            "AIzaSyA354CsHWXlT7WGq_27nJe95KNm0u7a-Mg",
            "f625daf1db8f54cd9",
            sendQuery
        )
        client.enqueue(object : Callback<PenjelajahResponse> {
            override fun onResponse(
                call: Call<PenjelajahResponse>,
                response: Response<PenjelajahResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.e("KoleksiActivity", "body: ${responseBody}")
                    if (responseBody != null) setItems(responseBody)
                } else {
                    Log.e("KoleksiActivity", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PenjelajahResponse>, t: Throwable) {
                showLoading(false)
                Log.e("KoleksiActivity", "onFailure: ${t.message}")
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setItems(response: PenjelajahResponse) {
        var items = ArrayList<Penjelajah>()

        for ((index, item) in response.items.withIndex()) {
            var imageUrl = ""
            if (item.pagemap.cseThumbnail != null)
                if (item.pagemap.cseThumbnail.isNotEmpty()) imageUrl = item.pagemap.cseThumbnail[0].src

            items.add(
                Penjelajah(
                    uuid = index.toString(),
                    title = item.title,
                    description = item.snippet,
                    url = item.link,
                    imageUrl = imageUrl
                )
            )
        }

        itemsSearch = items

        activityBinding.rvPenjelajah.layoutManager = LinearLayoutManager(this)
        val adapter = ListPenjelajahAdapter(itemsSearch)
        activityBinding.rvPenjelajah.adapter = adapter

        adapter.setOnItemClickCallback(object : ListPenjelajahAdapter.OnItemClickCallback {
            override fun onItemClicked(penjelajah: Penjelajah) {
                showSelectedBuku(penjelajah.url)
            }
        })

        activityBinding.rvPenjelajah.setOnTouchListener(object: OnSwipeTouchListener(this) {
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
                        if (Utils.executeVoiceCommand(this, recognizedText.lowercase())) {
                            val command = recognizedText.lowercase()
                            val arrCommand = command.split(" ").toMutableList()

                            if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                                arrCommand.removeAt(0)
                                val textToSearch = arrCommand.joinToString(" ")

                                activityBinding.penjelajahSearchField.setText(textToSearch)
                                search(textToSearch)
                            }
                            else if (
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
                                    val listBooks = itemsSearch
                                    val selectedBook = listBooks.find { it ->
                                        it.title
                                            .toLowerCase()
                                            .replace("[^A-Za-z0-9 ]".toRegex(), "")
                                            .replace("\\s+".toRegex(), "")
                                            .contains(
                                                titleToSearch
                                                    .replace("[^A-Za-z0-9 ]".toRegex(), "")
                                                    .replace("\\s+".toRegex(), "")
                                            ) ||
                                                titleToSearch
                                                    .replace("[^A-Za-z0-9 ]".toRegex(), "")
                                                    .replace("\\s+".toRegex(), "")
                                                    .contains(
                                                        it.title
                                                            .toLowerCase()
                                                            .replace("[^A-Za-z0-9 ]".toRegex(), "")
                                                            .replace("\\s+".toRegex(), "")
                                                    )
                                    }
                                    if (selectedBook != null) {
                                        showSelectedBuku(selectedBook.url)
                                    } else {
                                        val textError =
                                            "Judul buku \"$title\" tidak ditemukan. Silahkan coba lagi."
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

    fun showSelectedBuku(url: String) {
        var regex = "\\.(pdf)\$".toRegex()
        var urlLowerCase = url?.lowercase()

        if (regex.containsMatchIn(""+urlLowerCase)) {
            val moveIntent = Intent(this, BukuActivity::class.java)
            moveIntent.putExtra("SelectedBookID", "ltk-p-"+url)
            moveIntent.putExtra("SelectedBookUrl", ""+url)
            moveIntent.putExtra("SelectedBookLastPage", 0)
            this.startActivity(moveIntent)
        } else {
            Toast.makeText(this, "GAGAL: Bukan file PDF -> "+urlLowerCase, Toast.LENGTH_SHORT).show()
        }
    }
}