package com.fractaldev.literaku

import com.google.gson.annotations.SerializedName

data class RiwayatResponse(

	@field:SerializedName("RiwayatResponse")
	val riwayatResponse: List<RiwayatResponseItem>
)

data class RiwayatResponseItem(

	@field:SerializedName("log")
	val log: String,

	@field:SerializedName("uuid")
	val uuid: String
)
