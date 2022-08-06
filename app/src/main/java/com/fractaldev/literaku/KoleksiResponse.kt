package com.fractaldev.literaku

import com.google.gson.annotations.SerializedName

data class KoleksiResponse(

	@field:SerializedName("KoleksiResponse")
	val koleksiResponse: List<KoleksiResponseItem>
)

data class KoleksiResponseItem(

	@field:SerializedName("cover")
	val cover: String,

	@field:SerializedName("year")
	val year: String,

	@field:SerializedName("author")
	val author: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("synopsis")
	val synopsis: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("url")
	val url: String
)
