package com.fractaldev.literaku

object Commands {
    // All Pages Commands
    internal var back = listOf<String>("kembali", "balik")
    internal var backToHome = listOf<String>(
        "kembali ke halaman utama",
        "balik ke halaman utama",
        "kembali ke halaman pertama",
        "balik ke halaman pertama",
        "kembali ke halaman awal",
        "balik ke halaman awal",
        "kembali ke awal",
        "balik ke awal"
    )
    internal var exit = listOf<String>("keluar", "keluar aplikasi", "tutup aplikasi")


    // MainActivity Commands
    internal var mainGoToPenjelajah = listOf<String>(
        "pergi ke halaman penjelajah",
        "buka halaman penjelajah",
        "akses halaman penjelajah",
        "pergi ke penjelajah",
        "buka penjelajah",
        "akses penjelajah"
    )
    internal var mainGoToRiwayat = listOf<String>(
        "pergi ke halaman riwayat",
        "buka halaman riwayat",
        "akses halaman riwayat",
        "pergi ke riwayat",
        "buka riwayat",
        "akses riwayat"
    )
    internal var mainGoToKoleksi = listOf<String>(
        "pergi ke halaman koleksi",
        "buka halaman koleksi",
        "akses halaman koleksi",
        "pergi ke koleksi",
        "buka koleksi",
        "akses koleksi"
    )
    internal var mainGoToPanduan = listOf<String>(
        "pergi ke halaman panduan",
        "buka halaman panduan",
        "akses halaman panduan",
        "pergi ke panduan",
        "buka panduan",
        "akses panduan"
    )
    internal var mainGoToBantuan = listOf<String>(
        "pergi ke halaman bantuan",
        "buka halaman bantuan",
        "akses halaman bantuan",
        "pergi ke bantuan",
        "buka bantuan",
        "akses bantuan",
        "bukabantuan"
    )


    // BukuActivity Commands
    internal var bukuNextPage = listOf<String>(
        "pergi ke halaman berikutnya",
        "buka halaman berikutnya",
        "ke halaman berikutnya",
        "halaman berikutnya",
        "berikutnya",
        "pergi ke halaman selanjutnya",
        "buka halaman selanjutnya",
        "ke halaman selanjutnya",
        "halaman selanjutnya",
        "selanjutnya"
    )
    internal var bukuPrevPage = listOf<String>(
        "pergi ke halaman sebelumnya",
        "buka halaman sebelumnya",
        "ke halaman sebelumnya",
        "halaman sebelumnya",
        "sebelumnya"
    )
    internal var bukuStopRead = listOf<String>(
        "berhenti",
        "berhenti membaca"
    )
    internal var bukuResumeRead = listOf<String>(
        "lanjut",
        "lanjutkan",
        "lanjutkan membaca"
    )
    internal var bukuGoToFirstPage = listOf<String>(
        "pergi ke halaman pertama",
        "buka halaman pertama",
        "ke halaman pertama",
        "kembali ke halaman pertama"
    )
}