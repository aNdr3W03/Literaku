package com.fractaldev.literaku

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Buku (
    var title: String = "",
    var description: String = "",
    var author: String = ""
) : Parcelable