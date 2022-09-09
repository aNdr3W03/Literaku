package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.UtteranceProgressListener
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import com.fractaldev.literaku.databinding.ActivityBukuBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import java.io.File

class BukuActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityBukuBinding
    private var helpers = Helpers(this)

    lateinit var mDialog: Dialog
    private var textBantuan: String = ""

    private var selectedBookID: String = ""
    private var selectedBookUrl: String = ""
    private var selectedBookTitle: String = ""
    private var selectedBookLastPage: Int = 0

    lateinit var reader: PdfReader
    private var textToRead: List<String> = emptyList()
    private var currentTextToRead: String = ""
    private var currentPageToRead: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityBukuBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setToolbar()
        setMenu()
        getResourceBantuan()

        helpers.textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_stop_24
                    )
                )
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_play_arrow_24
                    )
                )
            }

            override fun onDone(utteranceId: String?) {
                readPage()
            }

            override fun onError(utteranceId: String?) {}
        })

        // Voice Command Gesture
        activityBinding.dummyScreen.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                playPauseRead("stop")
                helpers.activateVoiceCommand()
            }
            override fun onSwipeRight() {
                super.onSwipeLeft()
                playPauseRead("stop")
                helpers.activateVoiceCommand()
            }
        })

        if (intent != null) {
            activityBinding.progressBar.visibility = View.VISIBLE
            selectedBookUrl = intent.getStringExtra("SelectedBookUrl").toString()
            selectedBookID = intent.getStringExtra("SelectedBookID").toString()
            selectedBookTitle = intent.getStringExtra("SelectedBookTitle").toString()
            selectedBookLastPage = intent.getIntExtra("SelectedBookLastPage", 0)

            if (
                (!TextUtils.isEmpty(selectedBookUrl) || selectedBookUrl != null) &&
                (!TextUtils.isEmpty(selectedBookID) || selectedBookID != null)
            ) {
                // Get File from URL String
                FileLoader.with(this)
                    .load(selectedBookUrl, false)
                    .fromDirectory("PDFFile", FileLoader.DIR_INTERNAL)
                    .asFile(object:FileRequestListener<File>{
                        override fun onLoad(
                            request: FileLoadRequest?,
                            response: FileResponse<File>?
                        ) {
                            // After fetch file, initialize show and read PDF
                            val pdfFile = response!!.body

                            getPDFView(pdfFile, selectedBookLastPage)
                            activityBinding.progressBar.visibility = View.GONE

                            getPDFRead(pdfFile.toUri(), selectedBookLastPage+1)
                        }
                        override fun onError(request: FileLoadRequest?, t: Throwable?) {
                            Toast.makeText(this@BukuActivity, ""+t!!.message, Toast.LENGTH_SHORT).show()
                            helpers.speak("Gagal membuka: "+t!!.message)
                            activityBinding.progressBar.visibility = View.GONE
                        }
                    })
            }

            activityBinding.progressBar.visibility = View.GONE
        }
    }

    fun setToolbar() {
        activityBinding.backToolbar.backBtn.setOnClickListener {
            finish()
        }
        activityBinding.backToolbar.settingBtn.setOnClickListener {
            val moveIntent = Intent(this@BukuActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
        activityBinding.include.fabBantuan.setOnClickListener {
            openBantuan()
        }
    }

    fun setMenu() {
        activityBinding.fabNextPage.setOnClickListener {
            nextPage()
        }
        activityBinding.fabPreviousPage.setOnClickListener {
            prevPage()
        }
        activityBinding.fabPlayPause.setOnClickListener {
            playPauseRead()
        }
    }

    private fun getResourceBantuan() {
        var arrText: MutableList<String> = mutableListOf()
        arrText.add(resources.getString(R.string.bantuanBuku0))
        arrText.add(resources.getString(R.string.bantuanBuku1))
        arrText.add(resources.getString(R.string.bantuanBuku2))
        arrText.add(resources.getString(R.string.bantuanBuku3))
        arrText.add(resources.getString(R.string.bantuanBuku4))
        arrText.add(resources.getString(R.string.bantuanBuku5))
        arrText.add(resources.getString(R.string.bantuanBuku6))
        arrText.add(resources.getString(R.string.bantuanBuku7))

        textBantuan = arrText.joinToString(" ")
    }

    private fun openBantuan() {
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.bantuan_buku)
        mDialog.show()

        mDialog.setOnDismissListener {
            helpers.textToSpeechEngine.stop()
            readPage()
        }

        helpers.speak(textBantuan)
    }

    fun getPDFView(file: File, lastPage: Int = 0) {
        activityBinding.pdfView.fromFile(file)
            .password(null) // password file pdf
            .defaultPage(0) // jump to page (last page) - still not working
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onDraw{canvas, pageWidth, pageHeight, displayedPage ->
                // Kode
            }.onDrawAll{canvas, pageWidth, pageHeight, displayedPage ->
                // Kode
            }
            .onPageChange{page, pageCount ->
                // Kode
            }.onPageError{page, t ->
                // Kode Error
                Log.d("ERROR", "" + t.localizedMessage)
            }
            .onTap{ false }
            .onRender{nbPages, pageWidth, pageHeight ->
                activityBinding.pdfView.fitToWidth(lastPage)
            }
            .spacing(4)
            .enableAnnotationRendering(true)
            .invalidPageColor(Color.RED)
            .load()
    }

    fun getPDFRead(uri: Uri, lastPage: Int = 1) {
        reader = PdfReader(contentResolver.openInputStream(uri))

        val lastPageToRead = if (lastPage == 0) 1 else lastPage
        setPageContent(lastPageToRead)
    }

    private fun setPageContent(pageNo: Int) {
        if (pageNo <= reader.numberOfPages) {
            currentPageToRead = pageNo

            var textFromPDF = PdfTextExtractor.getTextFromPage(
                reader,
                pageNo
            )

            // Char limit to read - 4000 char
            // For now, just set list of string with 3500 char each string
            var arrTextFromPDF = Utils.splitIntoChunks(3500, textFromPDF)
            textToRead = arrTextFromPDF

            activityBinding.pdfView.jumpTo(currentPageToRead - 1)
            readPage()
        } else {
            textToRead = emptyList()
        }
    }

    private fun readPage() {
        if (textToRead.isNotEmpty()) {
            currentTextToRead = textToRead[0]
            textToRead = Utils.removeElementByIndex(textToRead, 0) as List<String>

            helpers.speak(currentTextToRead)
        } else {
            setPageContent(++currentPageToRead)
        }
    }

    private fun playPauseRead() {
        if (::reader.isInitialized) {
            if (helpers.textToSpeechEngine.isSpeaking) {
                helpers.textToSpeechEngine.stop()
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_play_arrow_24
                    )
                )

            } else {
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_stop_24
                    )
                )
                helpers.speak(currentTextToRead)
            }
        }
    }
    private fun playPauseRead(overrideAction: String? = "") {
        if (::reader.isInitialized) {
            if (overrideAction == "stop") {
                helpers.textToSpeechEngine.stop()
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_play_arrow_24
                    )
                )

            } else if (overrideAction == "play") {
                activityBinding.fabPlayPause.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@BukuActivity,
                        R.drawable.ic_baseline_stop_24
                    )
                )
                helpers.speak(currentTextToRead)
            }
        }
    }

    private fun nextPage() {
        if (::reader.isInitialized) {
            if (currentPageToRead < reader.numberOfPages) {
                if (helpers.textToSpeechEngine.isSpeaking) {
                    helpers.textToSpeechEngine.stop()
                }

                setPageContent(++currentPageToRead)
            }
        }
    }

    private fun prevPage() {
        if (::reader.isInitialized) {
            if (1 < currentPageToRead) {
                if (helpers.textToSpeechEngine.isSpeaking) {
                    helpers.textToSpeechEngine.stop()
                }

                setPageContent(--currentPageToRead)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveLastReadPage()
        helpers.textToSpeechEngine.shutdown()
    }

    private fun saveLastReadPage() {
        val bookToSave = Buku(
            title = selectedBookTitle,
            bookUrl = selectedBookUrl,
            lastPage = currentPageToRead - 1
        )

        helpers.setHistory(bookToSave)
    }


    // Commands - Override from Utils Commands
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            helpers.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]

                        if (helpers.executeVoiceCommand(recognizedText.lowercase())) {
                            // Override
                            if (recognizedText.lowercase() != "") {
                                val command = recognizedText.lowercase()

                                if (Commands.openBantuan.contains(command)) {
                                    openBantuan()
                                }
                                else if (Commands.bukuNextPage.contains(command)) {
                                    nextPage()
                                }
                                else if (Commands.bukuPrevPage.contains(command)) {
                                    prevPage()
                                }
                                else if (Commands.bukuStopRead.contains(command)) {
                                    playPauseRead("stop")
                                }
                                else if (Commands.bukuResumeRead.contains(command)) {
                                    playPauseRead("play")
                                }
                                else if (Commands.bukuGoToFirstPage.contains(command)) {
                                    setPageContent(1)
                                }
                                else {
                                    playPauseRead("play")
                                }
                            }
                        }
                    }
                } else {
                    playPauseRead("play")
                }
            }
        }
    }
}
