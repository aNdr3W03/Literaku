package com.fractaldev.literaku

import android.app.Activity
import android.content.*
import android.content.Context
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.preference.PreferenceManager
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

    fun getSettingsValue(key: String?, context: Context?): String? {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        return preferences.getString(key, null)
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

    fun convertTextToNumber(text: String = ""): Int? {
        if (text != "") {
            var existNumberTextRes: MutableList<List<String>> = Numbers.getAllNumbersText()

            existNumberTextRes.forEach {
                if (it.contains(
                    text.toLowerCase()
                        .replace("[^A-Za-z0-9 ]".toRegex(), "")
                        .replace("\\s+".toRegex(), "")
                        .replace("^ke".toRegex(), "")
                    )
                ) {
                    return it[0].toInt()
                }
            }

            return null
        }
        return null
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
            else if (Commands.openBantuan.contains(command)) {
                // Override - dialog bantuan based on activity
                return true
            }
            else if (Commands.mainGoToPengaturan.contains(command)) {
                val moveIntent = Intent(activity, SettingActivity::class.java)
                activity.startActivity(moveIntent)
            }

            // Activity Commands
            else {
                when (activityName) {
                    "MainActivity" -> {
                        val arrCommand = command.split(" ").toMutableList()
                        if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                            // Override
                            return true
                        }
                        else if (Commands.mainGoToPenjelajah.contains(command)) {
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
                        else {
                            val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                            Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                            // Override - because speak from activity
                            return true
                        }
                    }

                    "PenjelajahActivity" -> {
                        if (Commands.penjelajahReadAgain.contains(command)) {
                            // Override - because list of books is in activity variable
                            return true
                        }

                        val arrCommand = command.split(" ").toMutableList()
                        if (arrCommand != null) {
                            if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                                // Override
                                return true
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
                                // Override - because list of books is in activity variable
                                return true
                            }
                            else {
                                val textError = "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                                Toast.makeText(activity, textError, Toast.LENGTH_LONG).show()
                                // Override - because speak from activity
                                return true
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
                                // Override - because speak from activity
                                return true
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
                        // Override - because speak from activity
                        return true
                    }
                }
            }
        }

        return false
    }
}