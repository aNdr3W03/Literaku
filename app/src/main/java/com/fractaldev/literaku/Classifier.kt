package com.fractaldev.literaku

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

//class Classifier(context: Context, jsonFilename: String, inputMaxLen: Int) {
//    private var context: Context? = context
//
//    // Filename for the exported vocab (.json)
//    private var filename: String? = jsonFilename
//
//    // Max length of the input sequence for the given model.
//    private var maxlen: Int = inputMaxLen
//
//    private var vocabData: HashMap<String, Int> = HashMap<String, Int>()
//
////    private var aaaa: Int? = null
//
//    // Load the contents of the vocab (see assets/word_dict.json)
//    private fun loadJSONFromAsset(filename: String?): String? {
//        var json: String? = null
//        try {
//            val inputStream = context!!.assets.open(filename!!)
//            val size = inputStream.available()
//            Log.e("NLP0_1 size", "output: ${size}")
//            val buffer = ByteArray(size)
//            inputStream.read(buffer)
//            inputStream.close()
//            json = String(buffer)
//            Log.e("NLP0_2 json", "output: ${json}")
//        }
//        catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    fun processVocab(callback: VocabCallback) {
//        CoroutineScope(Dispatchers.Main).launch {
//            loadVocab(callback, loadJSONFromAsset(filename)!!)
//        }
//    }
//
//    // Tokenize the given sentence
//    fun tokenize(message: String): IntArray {
//        val parts: List<String> = message.split(" ")
//        Log.e("NLP4_1 parts", "output: $parts")
//
//        val tokenizedMessage = ArrayList<Int>()
//        Log.e("NLP4_2 tokenizedMessage", "output: $tokenizedMessage")
//
//        for (part in parts) {
//            if (part.trim() != "") {
//                var index: Int? = 0
//                Log.e("NLP4_3 part", "output: ${part}")
//                Log.e("NLP4_4 parts", "output: ${parts}")
//                Log.e("NLP4_5 index", "output: ${index}")
//                Log.e("NLP4_6 vocabData", "output: ${vocabData}")
////                Log.e("NLP4_6__ aaaa", "output: ${aaaa}")
//                Log.e("NLP4_7 ", "output: ${vocabData[part]}")
//
//                if (vocabData[part] == null) {
//                    index = 0
//                } else {
//                    index = vocabData[part]
//                }
//                tokenizedMessage.add(index!!)
//            }
//        }
//        return tokenizedMessage.toIntArray()
//    }
//
//    // Pad the given sequence to maxlen with zeros.
//    fun padSequence(sequence: IntArray): IntArray {
//        val maxlen = this.maxlen
//        if (sequence.size > maxlen) {
//            return sequence.sliceArray(0..maxlen)
//        }
//        else if (sequence.size < maxlen) {
//            val array = ArrayList<Int>()
//            array.addAll(sequence.asList())
//            for (i in array.size until maxlen){
//                array.add(0)
//            }
//            return array.toIntArray()
//        }
//        else {
//            return sequence
//        }
//    }
//
//    interface VocabCallback {
//        fun onVocabProcessed()
//    }
//
//    private fun loadVocab(callback: VocabCallback, json: String) {
//        with (Dispatchers.Default) {
//            val jsonObject = JSONObject(json)
//            Log.e("NLP4_6_1 jsonObject", "output: ${jsonObject}")
//            val iterator: Iterator<String> = jsonObject.keys()
//            Log.e("NLP4_6_2 iterator", "output: ${iterator}")
//            val data = HashMap<String,Int>()
//            Log.e("NLP4_6_3 data", "output: ${data}")
//
////            val abcd = 1
//
//            while (iterator.hasNext()) {
//                val key = iterator.next()
//                Log.e("NLP4_6_4 key", "output: ${key}")
//                data[key] = jsonObject.get(key) as Int
//            }
//            with (Dispatchers.Main){
//                vocabData = data
//                setVocabData(data)
//                Log.e("NLP4_6_5 vocabData", "output: ${vocabData}")
//                callback.onVocabProcessed()
//            }
//
////            aaaa = abcd
//        }
//    }
//
//    private fun setVocabData(str: HashMap<String, Int>) {
//        this.vocabData = str
//    }
//}

class Classifier  {
    private var context: Context? = null
    private var filename: String? = null
    private var callback: DataCallback? = null
    private var maxlen: Int? = null
    private var vocabData: HashMap<String, Int>? = null

    constructor(context: Context, jsonFilename: String){
        this.context = context
        this.filename = jsonFilename
    }

    fun loadData () {
        val loadVocabularyTask = LoadVocabularyTask(callback)
        loadVocabularyTask.execute(loadJSONFromAsset(filename))
    }

    private fun loadJSONFromAsset(filename: String?): String? {
        var json: String? = null
        try {
            val inputStream = context!!.assets.open(filename!!)
            val size = inputStream.available()
            Log.e("NLP0_1 size", "output: ${size}")
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
            Log.e("NLP0_2 json", "output: ${json}")
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun setCallback(callback: DataCallback) {
        this.callback = callback
    }

    fun tokenize (message: String): IntArray {
        val parts: List<String> = message.split(" ")
        Log.e("NLP4_1 parts", "output: $parts")

        val tokenizedMessage = ArrayList<Int>()
        Log.e("NLP4_2 tokenizedMessage", "output: $tokenizedMessage")

        for (part in parts) {
            if (part.trim() != ""){
                var index: Int? = 0

                Log.e("NLP4_3 part", "output: ${part}")
                Log.e("NLP4_4 parts", "output: ${parts}")
                Log.e("NLP4_5 index", "output: ${index}")
                Log.e("NLP4_6 vocabData", "output: ${vocabData}")
                Log.e("NLP4_7 ", "output: ${vocabData!![part]}")

                if (vocabData!![part] == null) {
                    index = 0
                }
                else {
                    index = vocabData!![part]
                }
                tokenizedMessage.add(index!!)
            }
        }
        return tokenizedMessage.toIntArray()
    }

    fun padSequence (sequence: IntArray): IntArray {
        val maxlen = this.maxlen
        if (sequence.size > maxlen!!) {
            return sequence.sliceArray(0..maxlen)
        }
        else if (sequence.size < maxlen) {
            val array = ArrayList<Int>()
            array.addAll(sequence.asList())
            for (i in array.size until maxlen){
                array.add(0)
            }
            return array.toIntArray()
        }
        else {
            return sequence
        }
    }

    fun setVocab(data: HashMap<String, Int>?) {
        this.vocabData = data
        Log.e("NLP4_6 vocabData", "output: ${vocabData}")
    }

    fun setMaxLength(maxlen: Int) {
        this.maxlen = maxlen
    }

    interface DataCallback {
        fun onDataProcessed(result: HashMap<String, Int>?)
    }

    private inner class LoadVocabularyTask(callback: DataCallback?): AsyncTask<String, Void, HashMap<String, Int>?>() {
        private var callback: DataCallback? = callback

        override fun doInBackground(vararg params: String?): HashMap<String, Int>? {
            val jsonObject = JSONObject(params[0])
            val iterator: Iterator<String> = jsonObject.keys()
            val data = HashMap<String, Int>()
            while (iterator.hasNext()) {
                val key = iterator.next()
                data.put(key, jsonObject.get(key) as Int)
            }
            return data
        }

        override fun onPostExecute(result: HashMap<String, Int>?) {
            super.onPostExecute(result)
            callback?.onDataProcessed(result)
        }
    }
}
