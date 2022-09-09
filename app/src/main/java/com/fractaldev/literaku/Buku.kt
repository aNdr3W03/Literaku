package com.fractaldev.literaku

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Buku(
    var uuid: String = "",
    var title: String = "",
    var description: String = "",
    var author: String = "",
    var year: String = "",
    var bookUrl: String = "",
    var coverURL: String = "",
    var lastPage: Int = 0,
    var lastRead: String = "00-00-0000",
) : Parcelable