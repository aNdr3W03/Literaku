package com.fractaldev.literaku

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // only URL Literaku
    @GET("ebook")
    fun getBooks(): Call<List<KoleksiResponseItem>>


    // only URL CSE
    @GET("v1?")
    fun getPenjelajahBooks(
        @Query("key") key: String = "AIzaSyA354CsHWXlT7WGq_27nJe95KNm0u7a-Mg",
        @Query("cx") cx: String =  "f625daf1db8f54cd9",
        @Query("q") query: String = ""
    ): Call<PenjelajahResponse>
}