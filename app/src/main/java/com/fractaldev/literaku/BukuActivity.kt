package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
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
import java.util.*

class BukuActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityBukuBinding
    lateinit var reader: PdfReader
    private var textToRead: List<String> = emptyList()
    private var currentTextToRead: String = ""
    private var currentPageToRead: Int = 0
    private var initialzedTTS: Boolean = false

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityBukuBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setToolbar()
        setMenu()

        textToSpeechEngine.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
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

        if (intent != null) {
            activityBinding.progressBar.visibility = View.VISIBLE
            val selectedBookUrl = intent.getStringExtra("SelectedBookUrl")
            val selectedBookID = intent.getStringExtra("SelectedBookID")
            val selectedBookLastPage = intent.getIntExtra("SelectedBookLastPage", 0)

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
                Log.d("ERROR", "" + t.localizedMessage);
            }
            .onTap{ false }
            .onRender{nbPages, pageWidth, pageHeight ->
                activityBinding.pdfView.fitToWidth(lastPage)
            }
            .spacing(4)
            .enableAnnotationRendering(true)
            .invalidPageColor(Color.RED)
            .load()

        activityBinding.pdfView.setOnClickListener {
            playPauseRead("stop")
            Utils.activateVoiceCommand(this@BukuActivity, REQUEST_CODE_STT)
        }
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

            speak(currentTextToRead)
        } else {
            setPageContent(++currentPageToRead)
        }
    }

    private fun playPauseRead() {
        if (::reader.isInitialized) {
            if (textToSpeechEngine.isSpeaking) {
                textToSpeechEngine.stop()
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
                speak(currentTextToRead)
            }
        }
    }
    private fun playPauseRead(overrideAction: String? = "") {
        if (::reader.isInitialized) {
            if (overrideAction == "stop") {
                textToSpeechEngine.stop()
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
                speak(currentTextToRead)
            }
        }
    }

    private fun nextPage() {
        if (::reader.isInitialized) {
            if (currentPageToRead < reader.numberOfPages) {
                if (textToSpeechEngine.isSpeaking) {
                    textToSpeechEngine.stop()
                }

                setPageContent(++currentPageToRead)
            }
        }
    }

    private fun prevPage() {
        if (::reader.isInitialized) {
            if (1 < currentPageToRead) {
                if (textToSpeechEngine.isSpeaking) {
                    textToSpeechEngine.stop()
                }

                setPageContent(--currentPageToRead)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechEngine.shutdown()
    }

    //Speaks the text with TextToSpeech
    private fun speak(text: String) =
        if (initialzedTTS)
            textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "PdfReader")
        else Handler().postDelayed({
            textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "PdfReader")
        }, 1250)


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
                            when (recognizedText.lowercase()) {
                                Commands.bukuNextPage[0], Commands.bukuNextPage[1], Commands.bukuNextPage[2], Commands.bukuNextPage[3], Commands.bukuNextPage[4], Commands.bukuNextPage[5], Commands.bukuNextPage[6], Commands.bukuNextPage[7], Commands.bukuNextPage[8], Commands.bukuNextPage[9], Commands.bukuNextPage[10], Commands.bukuNextPage[11] -> {
                                    nextPage()
                                }
                                Commands.bukuPrevPage[0], Commands.bukuPrevPage[1], Commands.bukuPrevPage[2], Commands.bukuPrevPage[3], Commands.bukuPrevPage[4], Commands.bukuPrevPage[5], Commands.bukuPrevPage[6] -> {
                                    prevPage()
                                }
                                Commands.bukuStopRead[0], Commands.bukuStopRead[1] -> {
                                    playPauseRead("stop")
                                }
                                Commands.bukuResumeRead[0], Commands.bukuResumeRead[1], Commands.bukuResumeRead[2] -> {
                                    playPauseRead("play")
                                }
                                Commands.bukuGoToFirstPage[0], Commands.bukuGoToFirstPage[1], Commands.bukuGoToFirstPage[2], Commands.bukuGoToFirstPage[3], Commands.bukuGoToFirstPage[4], Commands.bukuGoToFirstPage[5], Commands.bukuGoToFirstPage[6], Commands.bukuGoToFirstPage[7], Commands.bukuGoToFirstPage[8], Commands.bukuGoToFirstPage[9] -> {
                                    setPageContent(1)
                                }
                                else -> {
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
