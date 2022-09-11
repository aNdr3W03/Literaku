package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    // Object untuk fungsi umum yang dipakai dibanyak class
    // Contoh
    fun dummySum(a: Int, b: Int): Int {
        return a + b
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String = "\"dd-MM-yyyy\"", date: Date = Date("1/1/2000")): String {
        val dateFormatter: DateFormat = SimpleDateFormat(format)
        dateFormatter.isLenient = false

        return dateFormatter.format(date)
    }
    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String = "\"dd-MM-yyyy\"", date: String = "today"): String {
        val dateLwr = date.lowercase()

        val dateFormatter: DateFormat = SimpleDateFormat(format)
        dateFormatter.isLenient = false

        var dateToGet = Date("1/1/2000")

        when (dateLwr) {
            "today" -> {
                dateToGet = Date()
            }
            "yesterday" -> {
                dateToGet = Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24))
            }
        }

        return dateFormatter.format(dateToGet)
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

    fun WriteIO(fileName: String, text: String) {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+fileName+".txt"

        try {
            val pw = PrintWriter(filePath)
            pw.println(text)
            pw.close()
        } catch (error: FileNotFoundException) {
            Log.e("error write", ""+error)
        }
    }

    fun ReadIO(fileName: String): String {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/"+fileName+".txt"
        val file = File(filePath)

        try {
            val scanner = Scanner(file)
            val stringBuilder = StringBuilder()
            var string: String = ""

            while(scanner.hasNextLine()) {
                string = scanner.nextLine()
                stringBuilder.append(string+"\n")
            }
            scanner.close()

            return stringBuilder.toString()
        } catch (error: FileNotFoundException) {
            Log.e("error read", ""+error)
        }

        return ""
    }
}