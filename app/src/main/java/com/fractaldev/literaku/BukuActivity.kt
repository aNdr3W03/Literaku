package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import java.io.File

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor

import com.fractaldev.literaku.databinding.ActivityBukuBinding
import java.util.*

class BukuActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityBukuBinding
    lateinit var reader: PdfReader
    lateinit var textToRead: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityBukuBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setToolbar()

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

                            getPDFRead(pdfFile.toUri(), selectedBookLastPage)
                        }
                        override fun onError(request: FileLoadRequest?, t: Throwable?) {
                            Toast.makeText(this@BukuActivity, ""+t!!.message, Toast.LENGTH_SHORT).show()
                            activityBinding.progressBar.visibility = View.GONE
                        }
                    })
            }

            activityBinding.progressBar.visibility = View.GONE
        }

//        activityBinding.pdfView.setOnTouchListener(object: OnSwipeTouchListener(this) {
//            override fun onSwipeLeft() {
//                super.onSwipeLeft()
//                Utils.activateVoiceCommand(this@BukuActivity,
//                    PenjelajahActivity.REQUEST_CODE_STT
//                )
//            }
//            override fun onSwipeRight() {
//                super.onSwipeLeft()
//                Utils.activateVoiceCommand(this@BukuActivity,
//                    PenjelajahActivity.REQUEST_CODE_STT
//                )
//            }
//        })
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
                activityBinding.pdfView.fitToWidth()
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
        Log.e("Buku", "page: "+pageNo)
        if (pageNo <= reader.numberOfPages) {
            textToRead = PdfTextExtractor.getTextFromPage(
                reader,
                pageNo
            )

            Log.e("Buku", "text: "+textToRead)

            speak(textToRead)
            Handler().postDelayed({
                speak(textToRead)
            }, 500)
        } else {
            textToRead = ""
        }
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale("id", "ID")
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechEngine.shutdown()
    }

    //Speaks the text with TextToSpeech
    private fun speak(text: String) =
        textToSpeechEngine.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "PdfReader")
}
