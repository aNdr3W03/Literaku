package com.fractaldev.literaku

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Buku(
    var uuid: String = "",
    var title: String = "",
    var description: String = "",
    var author: String = "",
    var year: String = "",
    var bookUrl: String = "",
    var coverURL: String = ""
) : Parcelable