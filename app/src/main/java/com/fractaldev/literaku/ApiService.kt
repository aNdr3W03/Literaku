package com.fractaldev.literaku

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("ebook")
    fun getBooks(): Call<List<KoleksiResponseItem>>
}