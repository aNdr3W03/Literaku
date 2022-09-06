package com.fractaldev.literaku

import android.app.Activity
import android.content.*
import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.util.*


internal class Helpers(var context: Context) {
    // Sebagai pembantu class activity dalam menampung method dan properties umum
    // Sebelumnya: class Utils -> pembedanya, menampung context dan activity untuk mempermudah
    // Contoh:
    fun dummySum(a: Int, b: Int): Int {
        return a + b
    }

    private val activityFromContext: Activity = getActivityFromContext(context)!!
    private lateinit var activityBinding: Any

    private fun getActivityFromContext(context: Context): Activity? {
        if (context is ContextWrapper) {
            return if (context is Activity) {
                (context as Activity)
            } else {
                getActivityFromContext((context as ContextWrapper).baseContext)
            }
        }

        return null
    }

    internal val REQUEST_CODE_STT = 1
    private var initializedTTS: Boolean = false
    internal val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(context,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale("id", "ID")
                    setTextToSpeechSpeed()

                    initializedTTS = true
                }
            })
    }
    internal fun setTextToSpeechSpeed(customValue: Float? = null) {
        if (customValue == null) {
            val speedSpeech = getSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH))

            if (speedSpeech != null) {
                var speedSpeechInFloat = speedSpeech.toFloatOrNull()
                if (speedSpeechInFloat == null) speedSpeechInFloat = 1F
                textToSpeechEngine.setSpeechRate(speedSpeechInFloat)
            }
        }
        else {
            textToSpeechEngine.setSpeechRate(customValue)
        }
    }

    internal fun <T>setActivityBinding(activityBinding: T) {
        this.activityBinding = activityBinding!!
    }

    internal fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) =
        if (initializedTTS)
            textToSpeechEngine.speak(text.trim(), queueMode, null, "tts1")
        else Handler().postDelayed({
            textToSpeechEngine.speak(text.trim(), queueMode, null, "tts1")
        }, 750)

    internal fun getSettingsValue(key: String?): String? {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(key, null)
    }
    internal fun <T> setSettingsValue(key: String? = null, newValue: T? = null) {
        if (newValue != null && key != null) {
            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreference.registerOnSharedPreferenceChangeListener(spChanged)

            val editor = sharedPreference.edit()
            when (newValue) {
                is String -> editor.putString(key, newValue)
                is Int -> editor.putInt(key, newValue)
                is Float -> editor.putFloat(key, newValue)
                is Boolean -> editor.putBoolean(key, newValue)
            }
            editor.apply()

            sharedPreference.unregisterOnSharedPreferenceChangeListener(spChanged)
        }
    }
    var spChanged =
        OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == context.resources.getString(R.string.KEY_SPEED_SPEECH)) {
                setTextToSpeechSpeed()
            }
        }

    internal fun increaseSpeedSpeech() {
        val value = getSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH))
        var valueInFloat = value?.toFloat()

        if (valueInFloat != null) {
            if (valueInFloat.plus(0.35F) <= 1.7F) {
                val newValueInFloat = valueInFloat.plus(0.35F)
                val newValue = newValueInFloat.toString() + "F"

                setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), newValue)
            } else {
                setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), "1.7F")
            }
        }
    }
    internal fun decreaseSpeedSpeech() {
        val value = getSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH))
        val valueInFloat = value?.toFloat()

        if (valueInFloat != null) {
            if (valueInFloat.minus(0.35F) >= 0.3F) {
                val newValueInFloat = valueInFloat.minus(0.35F)
                val newValue = newValueInFloat.toString() + "F"

                setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), newValue)
            } else {
                setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), "0.3F")
            }
        }
    }

    fun activateVoiceCommand() {
        val language = "id-ID"
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
        sttIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
        sttIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,language)

        try {
            activityFromContext.startActivityForResult(sttIntent, REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(activityFromContext, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    fun executeVoiceCommand(command: String = ""): Boolean {
        val activityName = activityFromContext.localClassName

        // Will be long code for all Commands
        // if want to override in activity code - give "return true"
        if (command != "") {
            val reformatCommand = command.lowercase()
            val arrCommand = reformatCommand.split(" ").toMutableList()

            // Common-Commands
            if (Commands.back.contains(command)) {
                activityFromContext.finish()
            }
            else if (Commands.backToHome.contains(command)) {
                val moveIntent = Intent(activityFromContext, MainActivity::class.java)
                activityFromContext.startActivity(moveIntent)
            }
            else if (Commands.exit.contains(command)) {
                activityFromContext.finishAffinity()
            }
            else if (Commands.openBantuan.contains(command)) {
                // Override - dialog bantuan based on activity
                return true
            }
            else if (Commands.openPengaturan.contains(command)) {
                val moveIntent = Intent(activityFromContext, SettingActivity::class.java)
                activityFromContext.startActivity(moveIntent)
            }

            else if (Commands.increaseSpeed.contains(command)) {
                increaseSpeedSpeech()
                if (!textToSpeechEngine.isSpeaking)
                    speak("Berikut contoh kecepatan suara")
            }
            else if (Commands.decreaseSpeed.contains(command)) {
                decreaseSpeedSpeech()
                if (!textToSpeechEngine.isSpeaking)
                    speak("Berikut contoh kecepatan suara")
            }

            // Activity Commands
            else {
                when (activityName) {
                    "MainActivity" -> {
                        if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                            // Override
                            return true
                        } else if (Commands.mainGoToPenjelajah.contains(command)) {
                            val moveIntent =
                                Intent(activityFromContext, PenjelajahActivity::class.java)
                            activityFromContext.startActivity(moveIntent)
                        } else if (Commands.mainGoToRiwayat.contains(command)) {
                            val moveIntent =
                                Intent(activityFromContext, RiwayatActivity::class.java)
                            activityFromContext.startActivity(moveIntent)
                        } else if (Commands.mainGoToKoleksi.contains(command)) {
                            val moveIntent =
                                Intent(activityFromContext, KoleksiActivity::class.java)
                            activityFromContext.startActivity(moveIntent)
                        } else if (Commands.mainGoToPanduan.contains(command)) {
                            val moveIntent =
                                Intent(activityFromContext, PanduanActivity::class.java)
                            activityFromContext.startActivity(moveIntent)
                        } else {
                            val textError =
                                "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                            Toast.makeText(activityFromContext, textError, Toast.LENGTH_LONG).show()
                            speak(textError)
                        }
                    }

                    "PenjelajahActivity" -> {
                        if (Commands.penjelajahReadAgain.contains(command)) {
                            // Override - because list of books is in activity variable
                            return true
                        }
                        else if (arrCommand[0] == "cari" || arrCommand[0] == "mencari") {
                            // Override
                            return true
                        } else if (
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
                        } else {
                            val textError =
                                "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                            Toast.makeText(activityFromContext, textError, Toast.LENGTH_LONG).show()
                            speak(textError)
                        }
                    }

                    "RiwayatActivity" -> {
                        // TODO add voice commands for riwayat activity
                    }

                    "KoleksiActivity" -> {
                        if (Commands.koleksiReadAgain.contains(command)) {
                            // Override - because list of books is in activity variable
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
                        } else {
                            val textError =
                                "Perintah \"$command\" tidak dikenal. Silahkan coba lagi."
                            Toast.makeText(activityFromContext, textError, Toast.LENGTH_LONG).show()
                            speak(textError)
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
                        Toast.makeText(activityFromContext, textError, Toast.LENGTH_LONG).show()
                        speak(textError)
                    }
                }
            }
        }

        return false
    }


    // Custom Dirty Function ()
    // ...
}