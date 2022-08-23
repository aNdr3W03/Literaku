package com.fractaldev.literaku

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import java.util.*


object Utils {
    // Object untuk fungsi umum yang dipakai dibanyak class
    // Contoh
    fun dummySum(a: Int, b: Int): Int {
        return a + b
    }

        fun getActivity(context: Context?): Activity? {
        if (context == null) {
            return null
        } else if (context is ContextWrapper) {
            return if (context is Activity) {
                context
            } else {
                getActivity((context as ContextWrapper).baseContext)
            }
        }
        return null
    }

    fun splitIntoChunks(max: Int, string: String): List<String> = ArrayList<String>(string.length / max + 1).also {
        var firstWord = true
        val builder = StringBuilder()

        // split string by whitespace
        for (word in string.split(Regex("( |\n|\r|\n\r)+"))) {
            // if the current string exceeds the max size
            if (builder.length + word.length > max) {
                // then we add the string to the list and clear the builder
                it.add(builder.toString())
                builder.setLength(0)
                firstWord = true
            }
            // append a space at the beginning of each word, except the first one
            if (firstWord) firstWord = false else builder.append(' ')
            builder.append(word)
        }

        // add the last collected part if there was any
        if(builder.isNotEmpty()){
            it.add(builder.toString())
        }
    }

    fun removeElementByIndex(arr: List<*>, index: Int): List<*> {
        if (index < 0 || index >= arr.size) {
            return arr
        }

        val result = arr.toMutableList()
        result.removeAt(index)
        return result
    }

    fun activateVoiceCommand(activity: Activity, requestCodeSTT: Int) {
        val language = "id-ID"
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
        sttIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,language)

        try {
            activity.startActivityForResult(sttIntent, requestCodeSTT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(activity, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    fun executeVoiceCommand(activity: Activity, command: String = ""): Boolean {
        val activityName = activity.localClassName

        // Will be long code for all Commands
        // if want to override in activity code - give "return true"
        if (command != "") {
            // Common-Commands
            if (Commands.back.contains(command)) {
                activity.finish()
            }
            else if (Commands.backToHome.contains(command)) {
                val moveIntent = Intent(activity, MainActivity::class.java)
                activity.startActivity(moveIntent)
            }
            else if (Commands.exit.contains(command)) {
                activity.finishAffinity()
            }

            // Activity Commands
            else {
                when (activityName) {
                    "MainActivity" -> {
                        if (Commands.mainGoToPenjelajah.contains(command)) {
                            val moveIntent = Intent(activity, PenjelajahActivity::class.java)
                            activity.startActivity(moveIntent)
                        }
                        else if (Commands.mainGoToRiwayat.contains(command)) {
                            val moveIntent = Intent(activity, RiwayatActivity::class.java)
                            activity.startActivity(moveIntent)
                        }
                        else if (Commands.mainGoToKoleksi.contains(command)) {
                            val moveIntent = Intent(activity, KoleksiActivity::class.java)
                            activity.startActivity(moveIntent)
                        }
                        else if (Commands.mainGoToPanduan.contains(command)) {
                            val moveIntent = Intent(activity, PanduanActivity::class.java)
                            activity.startActivity(moveIntent)
                        }
                        else if (Commands.mainGoToBantuan.contains(command)) {
                            val mDialog = Dialog(activity)
                            mDialog.setContentView(R.layout.bantuan_home)
                            mDialog.show()
                        }
                        else {
                            val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                            Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                            speak(textError, activity)
                        }
                    }

                    "PenjelajahActivity" -> {
                        val elWebView = activity.findViewById<WebView>(R.id.elWebView)
                        val penjelajahSearchField = activity.findViewById<EditText>(R.id.penjelajahSearchField)

                        val arrCommand = command.split(" ").toMutableList()
                        if (arrCommand != null) {
                            if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                                arrCommand.removeAt(0)
                                val textToSearch = arrCommand.joinToString(" ")
                                val textToQuery = arrCommand.joinToString("+")

                                penjelajahSearchField.setText(textToSearch)

                                var sendQuery = ""
                                if (textToSearch != "" && textToSearch != null) sendQuery = "search?q=filetype%3Apdf+$textToQuery"
                                elWebView.loadUrl("https://www.google.com/$sendQuery")
                            }
                            else {
                                val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                                Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                                speak(textError, activity)
                            }
                        }
                    }

                    "RiwayatActivity" -> {
                        // TODO add voice commands for riwayat activity
                    }

                    "KoleksiActivity" -> {
                        val arrCommand = command.split(" ").toMutableList()

                        if (arrCommand != null) {
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
                                // Override - because list of books is in activity variable
                                return true
                            }
                            else {
                                val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                                Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                                speak(textError, activity)
                            }
                        }
                    }

                    "PanduanActivity" -> {
                        // TODO add voice commands for panduan activity
                    }

                    "BukuActivity" -> {
                        // Override - because pdfView element is in activity variable
                        return true
                    }

                    else -> {
                        val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                        Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                        speak(textError, activity)
                    }
                }
            }
        }

        return false
    }

    private fun speak (text: String, activity: Activity) {
        if (text.isNotEmpty()) {
            val textToSpeechEngine: TextToSpeech by lazy {
                TextToSpeech(activity,
                    TextToSpeech.OnInitListener { status ->
                        if (status == TextToSpeech.SUCCESS) {
//                                textToSpeechEngine.language = Locale("id", "ID")
                        }
                    })
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
            } else {
                textToSpeechEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }
}