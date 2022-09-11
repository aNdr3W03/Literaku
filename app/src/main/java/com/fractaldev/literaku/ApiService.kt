package com.fractaldev.literaku

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // only URL Literaku
    @GET("ebook")
    fun getBooks(): Call<List<KoleksiResponseItem>>

    @GET("history/{uuid}")
    fun getHistory(
        @Path("uuid") uuid: String
    ): Call<RiwayatResponseItem>

    @FormUrlEncoded
    @POST("history")
    fun addHistory(
        @Field("uuid") uuid: String,
        @Field("log") log: String
    ): Call<RiwayatResponseItem>

    @FormUrlEncoded
    @PUT("history/{uuid}")
    fun editHistory(
        @Path("uuid") uuid: String,
        @Field("log") log: String
    ): Call<RiwayatResponseItem>

    // only URL CSE
    @GET("v1?")
    fun getPenjelajahBooks(
        @Query("key") key: String = "AIzaSyA354CsHWXlT7WGq_27nJe95KNm0u7a-Mg",
        @Query("cx") cx: String =  "f625daf1db8f54cd9",
        @Query("q") query: String = ""
    ): Call<PenjelajahResponse>
}