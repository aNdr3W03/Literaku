package com.fractaldev.literaku

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Penjelajah(
    var uuid: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var imageUrl: String = ""
) : Parcelable