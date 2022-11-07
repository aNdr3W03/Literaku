package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.fractaldev.literaku.ml.Model
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CompletableDeferred
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Delegate
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder
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

    private fun increaseSpeedSpeech() {
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
        } else {
            setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), (1F + 0.35F).toString() + "F")
        }
    }
    private fun decreaseSpeedSpeech() {
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
        } else {
            setSettingsValue(context.resources.getString(R.string.KEY_SPEED_SPEECH), (1F - 0.35F).toString() + "F")
        }
    }

    internal suspend fun getAllHistory(): List<Buku>? {
        var books: List<Buku>? = listOf()
        val deviceLtkID: String? = getSettingsValue(context.resources.getString(R.string.KEY_DEV_LTK_ID))

        val res = CompletableDeferred<List<Buku>?>()

        if (deviceLtkID != null) {
            var jsonString = ""

            val client = ApiConfig.getApiService().getHistory(deviceLtkID)
            client.enqueue(object : Callback<RiwayatResponseItem> {
                override fun onResponse(
                    call: Call<RiwayatResponseItem>,
                    response: Response<RiwayatResponseItem>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) jsonString = responseBody.log
                        if (responseBody != null) jsonString = responseBody.log

                        if (jsonString != "") {
                            val gson = Gson()
                            val listOfMyClassObject: Type = object : TypeToken<ArrayList<Buku?>?>() {}.type

                            books = gson.fromJson(jsonString, listOfMyClassObject)

                            res.complete(books)
                        }
                    } else {
                        Log.e("History", "onFailureGET: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<RiwayatResponseItem>, t: Throwable) {
                    Log.e("History", "onFailureGET: ${t.message}")
                }
            })

            return res.await()
        }

        return books
    }
    @SuppressLint("SimpleDateFormat")
    internal suspend fun setHistory(book: Buku) {
        // Initialize
        val listHistory: List<Buku>? = getAllHistory()
        var mutableListHistory: MutableList<Buku> = mutableListOf()
        val todayDateString: String = Utils.getDate("dd-MM-yyyy", Date())   // Current date

        // Get Device LTK Id / Create new one
        var deviceLtkID: String? = getSettingsValue(context.resources.getString(R.string.KEY_DEV_LTK_ID))
        var isDeviceLtkIDExisted = true
        if (deviceLtkID == null) {
            deviceLtkID = "${UUID.randomUUID()}-${todayDateString}"
            setSettingsValue(context.resources.getString(R.string.KEY_DEV_LTK_ID), deviceLtkID)
            isDeviceLtkIDExisted = false
        }

        if (listHistory != null) {
            if (listHistory.isNotEmpty()) {
                mutableListHistory = listHistory.toMutableList()
                var selectedHistory: Buku? = mutableListHistory.find { it.bookUrl == book.bookUrl }
                var selectedHistoryIndex: Int = -1

                if (selectedHistory != null) {
                    // Kalau sudah pernah membaca buku tersebut
                    selectedHistoryIndex = mutableListHistory.indexOf(selectedHistory)

                    selectedHistory.lastPage = book.lastPage
                    selectedHistory.lastRead = todayDateString

                    mutableListHistory.removeAt(selectedHistoryIndex)
                    mutableListHistory.add(0, selectedHistory)
                } else {
                    // Kalau belum pernah membaca buku tersebut
                    selectedHistoryIndex = mutableListHistory.last().uuid.toInt() + 1

                    selectedHistory = Buku(
                        uuid = selectedHistoryIndex.toString(),
                        title = book.title,
                        bookUrl = book.bookUrl,
                        lastPage = book.lastPage,
                        lastRead = todayDateString
                    )

                    mutableListHistory.add(0, selectedHistory)
                }

                val gson = Gson()
                val jsonString = gson.toJson(mutableListHistory)

                // PUT (LTK ID existed in DB)
                val client = ApiConfig.getApiService().editHistory(
                    deviceLtkID,
                    jsonString
                )
                client.enqueue(object : Callback<RiwayatResponseItem> {
                    override fun onResponse(
                        call: Call<RiwayatResponseItem>,
                        response: Response<RiwayatResponseItem>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("History", "SUCCESS ADD HISTORY!")
                        } else {
                            Log.e("History", "onFailureEDIT1: ${response.message()}")
                        }
                    }
                    override fun onFailure(call: Call<RiwayatResponseItem>, t: Throwable) {
                        Log.e("History", "onFailureEDIT1: ${t.message}")
                    }
                })
            } else {
                val history = Buku(
                    uuid = "0",
                    title = book.title,
                    bookUrl = book.bookUrl,
                    lastPage = book.lastPage,
                    lastRead = todayDateString
                )
                mutableListHistory.add(history)

                val gson = Gson()
                val jsonString = gson.toJson(mutableListHistory)

                // POST (LTK ID not existed in DB)
                val client = ApiConfig.getApiService().addHistory(
                    deviceLtkID,
                    jsonString
                )
                client.enqueue(object : Callback<RiwayatResponseItem> {
                    override fun onResponse(
                        call: Call<RiwayatResponseItem>,
                        response: Response<RiwayatResponseItem>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("History", "SUCCESS ADD HISTORY!")
                        } else {
                            Log.e("History", "onFailureADD1: ${response.message()}")
                        }
                    }
                    override fun onFailure(call: Call<RiwayatResponseItem>, t: Throwable) {
                        Log.e("History", "onFailureADD1: ${t.message}")
                    }
                })
            }
        } else {
            // Kalau belum punya log riwayat sama sekali
            val history = Buku(
                uuid = "0",
                title = book.title,
                bookUrl = book.bookUrl,
                lastPage = book.lastPage,
                lastRead = todayDateString
            )
            mutableListHistory.add(history)

            val gson = Gson()
            val jsonString = gson.toJson(mutableListHistory)

            // POST (LTK ID not existed in DB)
            val client = ApiConfig.getApiService().addHistory(
                deviceLtkID,
                jsonString
            )
            client.enqueue(object : Callback<RiwayatResponseItem> {
                override fun onResponse(
                    call: Call<RiwayatResponseItem>,
                    response: Response<RiwayatResponseItem>
                ) {
                    if (response.isSuccessful) {
                        Log.d("History", "SUCCESS ADD HISTORY!")
                    } else {
                        Log.e("History", "onFailureADD2: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<RiwayatResponseItem>, t: Throwable) {
                    Log.e("History", "onFailureADD2: ${t.message}")
                }
            })
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

    // https://stackoverflow.com/questions/1252468/java-converting-string-to-and-from-bytebuffer-and-associated-problems
    var charset = Charset.forName("UTF-8")
//    var encoder: CharsetEncoder = charset.newEncoder()
//    var decoder: CharsetDecoder = charset.newDecoder()

    fun strToByteBuffer(msg: String): ByteBuffer? {
        return ByteBuffer.wrap(msg.toByteArray(charset!!))
    }

    fun byteBufferToStr(buffer: ByteBuffer): String? {
        val bytes: ByteArray
        if (buffer.hasArray()) {
            bytes = buffer.array()
        } else {
            bytes = ByteArray(buffer.remaining())
            buffer[bytes]
        }
        return String(bytes, charset!!)
    }

//    private val MODEL_ASSETS_PATH = "model.tflite"
//    private val INPUT_MAXLEN = 171
//    private var tfLiteInterpreter : Interpreter? = null
//
//    @Throws(IOException::class)
//    private fun loadModelFile(): MappedByteBuffer {
//        val assetFileDescriptor = assets.openFd(MODEL_ASSETS_PATH)
//        val fileInputStream = FileInputStream(assetFileDescriptor.getFileDescriptor())
//        val fileChannel = fileInputStream.getChannel()
//        val startoffset = assetFileDescriptor.getStartOffset()
//        val declaredLength = assetFileDescriptor.getDeclaredLength()
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
//    }
//
//    fun classifySequence (sequence : IntArray): FloatArray {
//        val interpreter = Interpreter(loadModelFile())
//        val inputs : Array<FloatArray> = arrayOf(sequence.map{it.toFloat()}.toFloatArray() )
//        val outputs : Array<FloatArray> = arrayOf(floatArrayOf(0.0f, 0.0f))
//        interpreter.run(inputs, outputs)
//        return outputs[0]
//    }

    fun executeVoiceCommand(command: String = ""): Boolean {
        val activityName = activityFromContext.localClassName

        // >> ---------- NLP MODEL ----------

        // References:
        // Create TF-Lite Model - https://youtu.be/GeGlGQ80mZg
        // Deploy TF-Lite Model - https://youtu.be/RJjiCwKAR8w
        var commandLength = command.length;
        Log.e("NLP1", "output: ${commandLength}")

        var byteBuffer : ByteBuffer = ByteBuffer.allocateDirect(1 * commandLength)
        Log.e("NLP2", "output: ${byteBuffer}")

        var cmdByteBuffer = strToByteBuffer(command);
        byteBuffer.put(cmdByteBuffer)

//        for (i in 0..commandLength) {
//            Log.e("NLP3_1", "output: ${command[i]}")
//            Log.e("NLP3_2", "output: ${i}")
//            byteBuffer.putChar(command[i]) // error?!
//
//            Log.e("NLP4_1", "output: ${byteBuffer}")
//        }

        Log.e("NLP4", "output: ${byteBuffer}")
        Log.e("NLP4", "output: ${byteBufferToStr(byteBuffer)}")

        val model = Model.newInstance(context)

        Log.e("NLP5", "output: ${model}")

        // Creates inputs for reference.
        // https://victordibia.com/blog/getting-started-android
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 19), DataType.STRING)
        val inputFeature0 = TensorBuffer.createDynamic(DataType.STRING)
        Log.e("NLP6_1", "output: ${inputFeature0}")

        inputFeature0.loadBuffer(byteBuffer)
        Log.e("NLP6_2", "output: ${inputFeature0}")

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        Log.e("NLP7_1", "output: ${outputs}")

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.toString()
        Log.e("NLP7_2", "output: ${outputFeature0}")

        // Releases model resources if no longer used.
        model.close()

        // ---------- NLP MODEL ---------- <<

//        // >> ---------- NLP MODEL ----------
//
//        val classifier = Classifier(context, "word_dict.json", INPUT_MAXLEN)
//        tfLiteInterpreter = Interpreter(loadModelFile())
//
//        val progressDialog = ProgressDialog(context)
//        progressDialog.setMessage("Parsing word_dict.json ...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//        classifier.processVocab(object: Classifier.VocabCallback {
//            override fun onVocabProcessed() {
//                // Processing done, dismiss the progressDialog.
//                progressDialog.dismiss()
//            }
//        })
//
//        // ---------- NLP MODEL ---------- <<

        // Will be long code for all Commands
        // if want to override in activity code - give "return true"
        if (command != "") {
            val reformatCommand = command.lowercase()
            val arrCommand = reformatCommand.split(" ").toMutableList()

            // Common-Commands
//            if (Commands.back.contains(command)) {
//                activityFromContext.finish()
//            }
            if (outputFeature0 == "back") {
                activityFromContext.finish()
            }
//            else if (Commands.backToHome.contains(command)) {
//                val moveIntent = Intent(activityFromContext, MainActivity::class.java)
//                activityFromContext.startActivity(moveIntent)
//            }
            else if (outputFeature0 == "backToHome") {
                val moveIntent = Intent(activityFromContext, MainActivity::class.java)
                activityFromContext.startActivity(moveIntent)
            }
//            else if (Commands.exit.contains(command)) {
//                activityFromContext.finishAffinity()
//            }
            else if (outputFeature0 == "exit") {
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
                        if (Commands.riwayatReadAgain.contains(command)) {
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