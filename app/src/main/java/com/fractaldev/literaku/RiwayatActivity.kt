package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.fractaldev.literaku.databinding.ActivityRiwayatBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.math.abs

class RiwayatActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityRiwayatBinding
    private lateinit var gestureDetector: GestureDetector
    private var helpers = Helpers(this)

    lateinit var mDialog: Dialog
    private var afterFirstTalk: Boolean = false // Temp Solve Bug

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var listBooks = ArrayList<Buku>()
    private var textBantuan: String = ""
    private var textBooks: String = ""

    private lateinit var listRiwayatAdapter: ListRiwayatAdapter

    override fun onPause() {
        helpers.textToSpeechEngine.stop()
        super.onPause()
    }
    override fun onDestroy() {
        helpers.textToSpeechEngine.shutdown()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)

        setToolbar()
        getResourceBantuan()
        initAdapter()

        helpers.textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)
            }

            override fun onDone(utteranceId: String?) {
                mDialog.dismiss()
            }

            override fun onError(utteranceId: String?) {}
        })

        fetchBooks()
    }

    fun setToolbar() {
        activityBinding.includeRiwayat1.settingBtn.setOnClickListener {
            val moveIntent = Intent(this@RiwayatActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
        activityBinding.includeRiwayat2.fabBantuan.setOnClickListener {
            openBantuan()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAdapter() {
        activityBinding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        listRiwayatAdapter = ListRiwayatAdapter(listBooks)
        activityBinding.rvRiwayat.adapter = listRiwayatAdapter

        listRiwayatAdapter.setOnItemClickCallback(object : ListRiwayatAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Buku) {
                showSelectedBuku(data)
            }
        })

        activityBinding.rvRiwayat.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                helpers.activateVoiceCommand()
            }
            override fun onSwipeRight() {
                super.onSwipeLeft()
                helpers.activateVoiceCommand()
            }
        })
    }

    private fun getResourceBantuan() {
        var arrText: MutableList<String> = mutableListOf()
        arrText.add(resources.getString(R.string.bantuanRiwayat0))
        arrText.add(resources.getString(R.string.bantuanRiwayat1))
        arrText.add(resources.getString(R.string.bantuanRiwayat2))
        arrText.add(resources.getString(R.string.bantuanRiwayat3))
        arrText.add(resources.getString(R.string.bantuanRiwayat4))

        textBantuan = arrText.joinToString(" ")
    }

    private fun openBantuan() {
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.bantuan_riwayat)
        mDialog.show()

        mDialog.setOnDismissListener {
            helpers.textToSpeechEngine.stop()
        }

        helpers.speak(textBantuan)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SimpleDateFormat")
    private fun fetchBooks() {
        showLoading(true)

//        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        var books: ArrayList<Buku>? = arrayListOf()
        GlobalScope.async {
            val resBooks = helpers.getAllHistory()

            if (resBooks != null)
                if (resBooks.isNotEmpty()) {
                    // TODO sort by date with code (not replace txt)
                    // var formatter = SimpleDateFormat("yyyy-MM-dd")
                    // resBooks.sortedByDescending { it -> formatter.parse(it.lastRead) } }

                    books = resBooks as ArrayList<Buku>

                    handler.post {
                        setListBooks(books!!)
                    }
                }
        }

        showLoading(false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setListBooks(books: ArrayList<Buku>) {
        listBooks = ArrayList<Buku>()
        listBooks = books

        for ((index, book) in books.withIndex()) {
            textBooks += "${index + 1}. ${book.title.trim()}. "
        }

        textBooks = if (textBooks != "") "Berikut daftar riwayat: $textBooks"
        else "Maaf, bacaan tidak ditemukan."

        listRiwayatAdapter.setData(listBooks)

        // Bug First Talk
        val textToSpeech = if (afterFirstTalk) textBooks else "anda memasuki halaman riwayat. $textBooks"
        helpers.speak(textToSpeech, TextToSpeech.QUEUE_ADD)
        afterFirstTalk = true
    }

    private fun showSelectedBuku(buku: Buku) {
        val moveIntent = Intent(this@RiwayatActivity, BukuActivity::class.java)
        moveIntent.putExtra("SelectedBookID", ""+buku.uuid)
        moveIntent.putExtra("SelectedBookUrl", ""+buku.bookUrl)
        moveIntent.putExtra("SelectedBookTitle", ""+buku.title)
        moveIntent.putExtra("SelectedBookLastPage", buku.lastPage)
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
                    helpers.activateVoiceCommand()
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
            helpers.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]

                        if (helpers.executeVoiceCommand(recognizedText.lowercase())) {
                            var command = recognizedText.lowercase()
                            var arrCommand = command.split(" ").toMutableList()

                            if (Commands.openBantuan.contains(command)) {
                                openBantuan()
                            }
                            else if (Commands.riwayatReadAgain.contains(command)) {
                                val textToSpeak = if (textBooks == "") "Maaf, bacaan tidak ditemukan"
                                else textBooks
                                helpers.speak(textToSpeak, TextToSpeech.QUEUE_ADD)
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
                                val restCommand = arrCommand.joinToString(" ")

                                var indexToSearch: Int? = null
                                indexToSearch = Utils.convertTextToNumber(restCommand)
                                val titleToSearch = arrCommand.joinToString("")

                                // SEARCH BY INDEX - First to search (prevent number in title if search by title)
                                if (indexToSearch != null) {
                                    val listBooks = listBooks
                                    var selectedBook: Buku? = null

                                    if (indexToSearch <= listBooks.size) {
                                        selectedBook = listBooks[indexToSearch - 1]
                                        showSelectedBuku(selectedBook)
                                    } else {
                                        val textError = "Buku \"$indexToSearch\" tidak ditemukan. Silahkan coba lagi."
                                        Toast.makeText(this, textError, Toast.LENGTH_LONG).show()
                                    }
                                } else if (titleToSearch != "") {
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
                                        val textError = "Judul buku \"$restCommand\" tidak ditemukan. Silahkan coba lagi."
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