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

    fun executeVoiceCommand(activity: Activity, command: String) {
        val activityName = activity.localClassName

        // Will be long code for all Command
        when (command) {
            // All
            Commands.back[0], Commands.back[1] -> activity.finish()
            Commands.backToHome[0], Commands.backToHome[1], Commands.backToHome[2], Commands.backToHome[3], Commands.backToHome[4], Commands.backToHome[5], Commands.backToHome[6], Commands.backToHome[7] -> if (activityName != "MainActivity") {
                val moveIntent = Intent(activity, MainActivity::class.java)
                activity.startActivity(moveIntent)
            }
            Commands.exit[0], Commands.exit[1], Commands.exit[2] -> activity.finishAffinity()

            // Else
            else -> {
                when (activityName) {
                    "MainActivity" -> {
                        when (command) {
                            Commands.mainGoToPenjelajah[0],  Commands.mainGoToPenjelajah[1], Commands.mainGoToPenjelajah[2], Commands.mainGoToPenjelajah[3], Commands.mainGoToPenjelajah[4], Commands.mainGoToPenjelajah[5] -> {
                                val moveIntent = Intent(activity, PenjelajahActivity::class.java)
                                activity.startActivity(moveIntent)
                            }
                            Commands.mainGoToRiwayat[0],  Commands.mainGoToRiwayat[1], Commands.mainGoToRiwayat[2], Commands.mainGoToRiwayat[3], Commands.mainGoToRiwayat[4], Commands.mainGoToRiwayat[5] -> {
                                val moveIntent = Intent(activity, RiwayatActivity::class.java)
                                activity.startActivity(moveIntent)
                            }
                            Commands.mainGoToKoleksi[0],  Commands.mainGoToKoleksi[1], Commands.mainGoToKoleksi[2], Commands.mainGoToKoleksi[3], Commands.mainGoToKoleksi[4], Commands.mainGoToKoleksi[5] -> {
                                val moveIntent = Intent(activity, KoleksiActivity::class.java)
                                activity.startActivity(moveIntent)
                            }
                            Commands.mainGoToPanduan[0],  Commands.mainGoToPanduan[1], Commands.mainGoToPanduan[2], Commands.mainGoToPanduan[3], Commands.mainGoToPanduan[4], Commands.mainGoToPanduan[5] -> {
                                val moveIntent = Intent(activity, PanduanActivity::class.java)
                                activity.startActivity(moveIntent)
                            }
                            Commands.mainGoToBantuan[0],  Commands.mainGoToBantuan[1], Commands.mainGoToBantuan[2], Commands.mainGoToBantuan[3], Commands.mainGoToBantuan[4], Commands.mainGoToBantuan[5], Commands.mainGoToBantuan[6] -> {
                                val mDialog = Dialog(activity)
                                mDialog.setContentView(R.layout.bantuan_home)
                                mDialog.show()
                            }
                            else -> {
                                val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                                Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                                speak(textError, activity)
                            }
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

                    }
                    "KoleksiActivity" -> {

                    }
                    "PanduanActivity" -> {

                    }
                    else -> {
                        val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                        Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                        speak(textError, activity)
                    }
                }
            }
        }
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