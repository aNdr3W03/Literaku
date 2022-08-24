package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class PenjelajahActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityPenjelajahBinding
    private lateinit var gestureDetector: GestureDetector
    lateinit var mDialog: Dialog
    private var initialzedTTS: Boolean = false

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var itemsSearch = ArrayList<Penjelajah>()
    private var textBantuan: String = ""
    private var textItems: String = ""

    companion object {
        internal const val REQUEST_CODE_STT = 1
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale("id", "ID")
                    initialzedTTS = true
                }
            })
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }
    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityPenjelajahBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setSearchField(activityBinding.penjelajahSearchField)

        gestureDetector = GestureDetector(this)

        setToolbar()
        getResourceBantuan()

        textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
            }

            override fun onDone(utteranceId: String?) {
                mDialog.dismiss()
            }

            override fun onError(utteranceId: String?) {}
        })

        firstTalkAfterOpen()
    }

    private fun setToolbar() {
        activityBinding.includePenjelajah2.fabBantuan.setOnClickListener {
            openBantuan()
        }
    }

    private fun getResourceBantuan() {
        var arrText: MutableList<String> = mutableListOf()
        arrText.add(resources.getString(R.string.bantuanPenjelajah0))
        arrText.add(resources.getString(R.string.bantuanPenjelajah1))
        arrText.add(resources.getString(R.string.bantuanPenjelajah2))
        arrText.add(resources.getString(R.string.bantuanPenjelajah3))
        arrText.add(resources.getString(R.string.bantuanPenjelajah4))
        arrText.add(resources.getString(R.string.bantuanPenjelajah5))

        textBantuan = arrText.joinToString(" ")
    }

    private fun openBantuan() {
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.bantuan_penjelajah)
        mDialog.show()

        mDialog.setOnDismissListener {
            textToSpeechEngine.stop()
        }

        speak(textBantuan)
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
        textItems = ""
        var sendQuery = ""
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
                    speak("Maaf, pencarian gagal.")
                }
            }
            override fun onFailure(call: Call<PenjelajahResponse>, t: Throwable) {
                showLoading(false)
                Log.e("KoleksiActivity", "onFailure: ${t.message}")
                speak("Maaf, pencarian gagal.")
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setItems(response: PenjelajahResponse) {
        var items = ArrayList<Penjelajah>()

        if (response.items != null)
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

                textItems += "${index + 1}. ${item.title.trim()}. "
            }

        itemsSearch = items
        textItems = if (textItems != "") "Berikut hasil pencarian: $textItems"
            else "Maaf, bacaan tidak ditemukan."

        activityBinding.rvPenjelajah.layoutManager = LinearLayoutManager(this)
        val adapter = ListPenjelajahAdapter(itemsSearch)
        activityBinding.rvPenjelajah.adapter = adapter

        speak(textItems)

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

                            if (Commands.openBantuan.contains(command)) {
                                openBantuan()
                            }
                            else if (Commands.penjelajahReadAgain.contains(command)) {
                                val textToSpeak = if (textItems == "") "Cari dahulu judul bacaan"
                                else textItems
                                speak(textToSpeak)
                            }
                            else if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
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
                                        speak(textError)
                                    }
                                }
                            }
                            else {
                                speak("Perintah \"$command\" tidak dikenal. Silahkan coba lagi.")
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
            speak("GAGAL: Bukan file PDF")
        }
    }

    private fun firstTalkAfterOpen() {
        var text = "anda memasuki halaman penjelajah."
        speak(text)
    }

    //Speaks the text with TextToSpeech
    private fun speak(text: String) =
        if (initialzedTTS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "tts1")
            } else {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null)
            }
        else Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "tts1")
            } else {
                textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null)
            }
        }, 1250)
}