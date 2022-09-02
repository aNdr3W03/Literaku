package com.fractaldev.literaku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import kotlin.math.abs

import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast

import com.fractaldev.literaku.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    lateinit var mDialog: Dialog
    private var initialzedTTS: Boolean = false

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private var textBantuan: String = ""

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale("id", "ID")

                    val speedSpeech = Utils.getSettingsValue("SPEED_SPEECH", this)
                    if (speedSpeech != null) {
                        var speedSpeechInFloat = speedSpeech.toFloatOrNull()
                        if (speedSpeechInFloat == null) speedSpeechInFloat = 1F
                        textToSpeechEngine.setSpeechRate(speedSpeechInFloat)
                    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)

        setToolbar()
        setMenu()
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

        // PDF Viewer
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: BaseMultiplePermissionsListener() {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    super.onPermissionsChecked(report)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    super.onPermissionRationaleShouldBeShown(permissions, token)
                }
            })

        // [BUG] - pertama kali harus panggil function 2 kali dengan jarak delay
        firstTalkAfterOpen()
        Handler().postDelayed({
            firstTalkAfterOpen()
        }, 250)
    }

    fun setToolbar() {
        activityBinding.includeMain1.settingBtn.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
        activityBinding.includeMain2.fabBantuan.setOnClickListener {
            openBantuan()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setMenu() {
        var listBtns: MutableList<Button> = mutableListOf()
        listBtns.add(activityBinding.btnPenjelajah)
        listBtns.add(activityBinding.btnRiwayat)
        listBtns.add(activityBinding.btnKoleksi)
        listBtns.add(activityBinding.btnPanduan)

        listBtns.forEach { btn ->
            lateinit var moveIntent: Intent
            btn.setOnClickListener {
                when (it) {
                    activityBinding.btnPenjelajah -> moveIntent = Intent(this@MainActivity, PenjelajahActivity::class.java)
                    activityBinding.btnRiwayat -> moveIntent = Intent(this@MainActivity, RiwayatActivity::class.java)
                    activityBinding.btnKoleksi -> moveIntent = Intent(this@MainActivity, KoleksiActivity::class.java)
                    activityBinding.btnPanduan -> moveIntent = Intent(this@MainActivity, PanduanActivity::class.java)
                }
                startActivity(moveIntent)
            }

            btn.setOnTouchListener(object: OnSwipeTouchListener(this) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    Utils.activateVoiceCommand(this@MainActivity,
                        REQUEST_CODE_STT
                    )
                }
                override fun onSwipeRight() {
                    super.onSwipeLeft()
                    Utils.activateVoiceCommand(this@MainActivity,
                        REQUEST_CODE_STT
                    )
                }
            })
        }
    }

    private fun getResourceBantuan() {
        var arrText: MutableList<String> = mutableListOf()
        arrText.add(resources.getString(R.string.bantuanHome0))
        arrText.add(resources.getString(R.string.bantuanHome1))
        arrText.add(resources.getString(R.string.bantuanHome2))
        arrText.add(resources.getString(R.string.bantuanHome3))
        arrText.add(resources.getString(R.string.bantuanHome4))
        arrText.add(resources.getString(R.string.bantuanHome5))
        arrText.add(resources.getString(R.string.bantuanHome6))
        arrText.add(resources.getString(R.string.bantuanHome7))

        textBantuan = arrText.joinToString(" ")
    }

    private fun openBantuan() {
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.bantuan_home)
        mDialog.show()

        mDialog.setOnDismissListener {
            textToSpeechEngine.stop()
        }

        speak(textBantuan)
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
                    Utils.activateVoiceCommand(this@MainActivity, REQUEST_CODE_STT)
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
                            else if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                                arrCommand.removeAt(0)
                                val textToSearch = arrCommand.joinToString(" ")

                                if (textToSearch != "")
                                    searchPenjelajah(textToSearch)
                                else {
                                    val textError = "Judul buku belum disebutkan. Silahkan coba lagi."
                                    Toast.makeText(this, textError, Toast.LENGTH_LONG).show()
                                    speak(textError)
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

    private fun searchPenjelajah(textToSearch: String) {
        val moveIntent = Intent(this, PenjelajahActivity::class.java)
        moveIntent.putExtra("textToSearch", textToSearch)
        this.startActivity(moveIntent)
    }

    private fun firstTalkAfterOpen() {
        var text = "selamat datang di aplikasi literaku. swipe layar ke kanan atau ke kiri untuk mengaktifkan perintah suara. lalu katakan buka bantuan untuk membuka bantuan"
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