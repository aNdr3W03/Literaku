package com.fractaldev.literaku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import java.io.File

class BukuActivity : AppCompatActivity() {
    lateinit var pdfView: PDFView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buku)
        setToolbar()

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBar)

        if (intent != null) {
            val viewType = intent.getStringExtra("ViewType")
            if (!TextUtils.isEmpty(viewType) || viewType != null) {
                // From Assets Folder
                if (viewType.equals("assets")) {
                    val selectedBook = intent.getStringExtra("SelectedBook")

                    if (!TextUtils.isEmpty(selectedBook) || selectedBook != null) {
                        val lastPageRead = intent.getIntExtra("LastPageRead", 0)
                        pdfView.fromAsset(selectedBook)
                            .password(null) // password file pdf
                            .defaultPage(lastPageRead) // jump to page (last page) - still not working
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
                                pdfView.fitToWidth()
                            }
                            .spacing(4)
                            .enableAnnotationRendering(true)
                            .invalidPageColor(Color.RED)
                            .load()
                    }
                }

                // From Internet (penjelajah)
                else if (viewType.equals("internet")) {
                    progressBar.visibility = View.VISIBLE

                    val selectedBook = intent.getStringExtra("SelectedBook")

                    if (!TextUtils.isEmpty(selectedBook) || selectedBook != null) {
                        FileLoader.with(this)
                            .load(selectedBook, false)
                            .fromDirectory("PDFFile", FileLoader.DIR_INTERNAL)
                            .asFile(object:FileRequestListener<File>{
                                override fun onLoad(
                                    request: FileLoadRequest?,
                                    response: FileResponse<File>?
                                ) {
                                    progressBar.visibility = View.GONE

                                    val pdfFile = response!!.body
                                    pdfView.fromFile(pdfFile)
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
                                            pdfView.fitToWidth()
                                        }
                                        .spacing(4)
                                        .enableAnnotationRendering(true)
                                        .invalidPageColor(Color.RED)
                                        .load()
                                }

                                override fun onError(request: FileLoadRequest?, t: Throwable?) {
                                    Toast.makeText(this@BukuActivity, ""+t!!.message, Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE
                                }

                            })
                    }
                }
            }
        }
    }

    fun setToolbar() {
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@BukuActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }
}