package com.fractaldev.literaku

import android.app.Activity
import android.content.*
import android.speech.RecognizerIntent
import android.widget.Toast
import java.util.*

object Utils {
    // Object untuk fungsi umum yang dipakai dibanyak class
    // Contoh
    fun dummySum(a: Int, b: Int): Int {
        return a + b
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
}