package com.fractaldev.literaku

object Commands {
    // All Pages Commands
    internal var back = listOf<String>("kembali", "balik")
    internal var backToHome = listOf<String>(
        "kembali ke halaman utama",
        "balik ke halaman utama",
        "buka halaman utama",
        "kembali ke halaman beranda",
        "balik ke halaman beranda",
        "buka halaman beranda",
        "kembali ke beranda",
        "balik ke beranda",
        "buka beranda",
    )
    internal var exit = listOf<String>("keluar", "keluar aplikasi", "tutup aplikasi")
    internal var openBantuan = listOf<String>(
        "pergi ke halaman bantuan",
        "membuka halaman bantuan",
        "buka halaman bantuan",
        "akses halaman bantuan",
        "pergi ke bantuan",
        "membuka bantuan",
        "buka bantuan",
        "akses bantuan",
        "bukabantuan"
    )
    internal var openPengaturan = listOf<String>(
        "pergi ke halaman pengaturan",
        "membuka halaman pengaturan",
        "buka halaman pengaturan",
        "akses halaman pengaturan",
        "pergi ke pengaturan",
        "membuka pengaturan",
        "buka pengaturan",
        "akses pengaturan"
    )

    internal var increaseSpeed = listOf<String>(
        "percepat",
        "percepat suara",
        "tambah kecepatan",
        "tambah kecepatan suara",
        "lebih cepat",
        "cepatkan suara",
        "cepatin suara",
        "cepetin suara",
        "cepatkan",
        "cepatin",
        "cepetin"
    )
    internal var decreaseSpeed = listOf<String>(
        "perlambat",
        "perlambat suara",
        "kurangi kecepatan",
        "kurangi kecepatan suara",
        "lebih lambat",
        "lambatkan suara",
        "lambatin suara",
        "lambatkan",
        "lambatin"
    )


    // MainActivity Commands
    internal var mainGoToPenjelajah = listOf<String>(
        "pergi ke halaman penjelajah",
        "membuka halaman penjelajah",
        "buka halaman penjelajah",
        "akses halaman penjelajah",
        "pergi ke penjelajah",
        "membuka penjelajah",
        "buka penjelajah",
        "akses penjelajah",

        "pergi ke halaman penjelajahan",
        "membuka halaman penjelajahan",
        "buka halaman penjelajahan",
        "akses halaman penjelajahan",
        "pergi ke penjelajahan",
        "membuka penjelajahan",
        "buka penjelajahan",
        "akses penjelajahan",

        "pergi ke halaman pencarian",
        "membuka halaman pencarian",
        "buka halaman pencarian",
        "akses halaman pencarian",
        "pergi ke pencarian",
        "membuka pencarian",
        "buka pencarian",
        "akses pencarian"
    )
    internal var mainGoToRiwayat = listOf<String>(
        "pergi ke halaman riwayat",
        "membuka halaman riwayat",
        "buka halaman riwayat",
        "akses halaman riwayat",
        "pergi ke riwayat",
        "membuka riwayat",
        "buka riwayat",
        "akses riwayat"
    )
    internal var mainGoToKoleksi = listOf<String>(
        "pergi ke halaman koleksi",
        "membuka halaman koleksi",
        "buka halaman koleksi",
        "akses halaman koleksi",
        "pergi ke koleksi",
        "membuka koleksi",
        "buka koleksi",
        "akses koleksi"
    )
    internal var mainGoToPanduan = listOf<String>(
        "pergi ke halaman panduan",
        "membuka halaman panduan",
        "buka halaman panduan",
        "akses halaman panduan",
        "pergi ke panduan",
        "membuka panduan",
        "buka panduan",
        "akses panduan"
    )


    // BukuActivity Commands
    internal var bukuNextPage = listOf<String>(
        "pergi ke halaman berikutnya",
        "buka halaman berikutnya",
        "baca halaman berikutnya",
        "ke halaman berikutnya",
        "halaman berikutnya",
        "berikutnya",
        "pergi ke halaman selanjutnya",
        "buka halaman selanjutnya",
        "baca halaman selanjutnya",
        "ke halaman selanjutnya",
        "halaman selanjutnya",
        "selanjutnya"
    )
    internal var bukuPrevPage = listOf<String>(
        "kembali ke halaman sebelumnya",
        "pergi ke halaman sebelumnya",
        "buka halaman sebelumnya",
        "baca halaman sebelumnya",
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
        "baca halaman pertama",
        "ke halaman pertama",
        "kembali ke halaman pertama",
        "pergi ke halaman awal",
        "buka halaman awal",
        "baca halaman awal",
        "ke halaman awal",
        "kembali ke halaman awal"
    )


    // Penjelajah
    internal var penjelajahReadAgain = listOf<String>(
        "baca ulang",
        "baca ulang hasil pencarian",
        "baca ulang daftar bacaan",
        "baca ulang daftar buku",
        "sebutkan ulang",
        "sebutkan ulang hasil pencarian",
        "sebutkan ulang daftar bacaan",
        "sebutkan ulang daftar buku",
    )


    // Koleksi
    internal var koleksiReadAgain = listOf<String>(
        "baca ulang",
        "baca ulang hasil pencarian",
        "baca ulang daftar bacaan",
        "baca ulang daftar buku",
        "sebutkan ulang",
        "sebutkan ulang hasil pencarian",
        "sebutkan ulang daftar bacaan",
        "sebutkan ulang daftar buku",
    )
}