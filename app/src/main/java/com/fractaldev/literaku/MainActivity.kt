package com.fractaldev.literaku

import android.Manifest
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener

class MainActivity : AppCompatActivity() {
    lateinit var mDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setMenu()

        // PDF Viewer
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: BaseMultiplePermissionsListener() {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    super.onPermissionsChecked(report)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    super.onPermissionRationaleShouldBeShown(permissions, token)
                }
            })
    }

    fun setToolbar() {
        val settingBtn = findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }

        val bantuanBtn = findViewById<FloatingActionButton>(R.id.fab_bantuan)
        bantuanBtn.setOnClickListener {
            mDialog = Dialog(this)
            mDialog.setContentView(R.layout.bantuan_home)
            mDialog.show()
        }
    }
    fun setMenu() {
        var btnPenjelajah = findViewById<Button> (R.id.btnPenjelajah)
        btnPenjelajah.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, PenjelajahActivity::class.java)
            startActivity(moveIntent)
        }
        var btnRiwayat = findViewById<Button> (R.id.btnRiwayat)
        btnRiwayat.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, RiwayatActivity::class.java)
            startActivity(moveIntent)
        }
        var btnKoleksi = findViewById<Button> (R.id.btnKoleksi)
        btnKoleksi.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, KoleksiActivity::class.java)
            startActivity(moveIntent)
        }
        var btnPanduan = findViewById<Button> (R.id.btnPanduan)
        btnPanduan.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, PanduanActivity::class.java)
            startActivity(moveIntent)
        }
    }
}