package com.fractaldev.literaku

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageButton
import com.github.barteksc.pdfviewer.PDFView

class BukuActivity : AppCompatActivity() {
    lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buku)
        setToolbar()

        pdfView = findViewById(R.id.pdfView)

        if (intent != null) {
            val viewType = intent.getStringExtra("ViewType")
            if (!TextUtils.isEmpty(viewType) || viewType != null) {
                if (viewType.equals("assets")) {

                    val lastPageRead = intent.getIntExtra("LastPageRead", 0)

                    pdfView.fromAsset("contoh-pdf.pdf")
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