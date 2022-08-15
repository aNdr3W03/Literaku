package com.fractaldev.literaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager

import com.fractaldev.literaku.databinding.ActivityRiwayatBinding
import kotlin.math.abs

class RiwayatActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var activityBinding: ActivityRiwayatBinding
    private lateinit var gestureDetector: GestureDetector
    private val list = ArrayList<Buku>()

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        gestureDetector = GestureDetector(this)

        setToolbar()

        activityBinding.rvRiwayat.setHasFixedSize(true)
        list.addAll(listBuku)
        showRecyclerList()
    }

    fun setToolbar() {
        activityBinding.includeRiwayat1.settingBtn.setOnClickListener {
            val moveIntent = Intent(this@RiwayatActivity, SettingActivity::class.java)
            startActivity(moveIntent)
        }
    }

    private val listBuku: ArrayList<Buku>
        get() {
            // DATA DUMMY
            val dataTitle = ArrayList<String>()
            dataTitle.add("1. The Covid-19 Epidemic")
            dataTitle.add("2. Rumah Baca Jendela...")
            dataTitle.add("3. Membangun Budaya L...")
            dataTitle.add("4. Strategi Peningkatan ...")

            val listBuku = ArrayList<Buku>()
            for (i in dataTitle.indices) {
                val hero = Buku(dataTitle[i], "Desc")
                listBuku.add(hero)
            }
            return listBuku
        }
    @SuppressLint("ClickableViewAccessibility")
    private fun showRecyclerList() {
        activityBinding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        val listRiwayatAdapter = ListRiwayatAdapter(list)
        activityBinding.rvRiwayat.adapter = listRiwayatAdapter

        listRiwayatAdapter.setOnItemClickCallback(object : ListRiwayatAdapter.OnItemClickCallback {
            override fun onItemClicked(buku: Buku) {
                showSelectedBuku(buku)
            }
        })

        activityBinding.rvRiwayat.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@RiwayatActivity,
                    PenjelajahActivity.REQUEST_CODE_STT
                )
            }
            override fun onSwipeRight() {
                super.onSwipeLeft()
                Utils.activateVoiceCommand(this@RiwayatActivity,
                    PenjelajahActivity.REQUEST_CODE_STT
                )
            }
        })
    }

    private fun showSelectedBuku(buku: Buku) {
        val moveIntent = Intent(this@RiwayatActivity, BukuActivity::class.java)
        moveIntent.putExtra("ViewType", "assets")
        moveIntent.putExtra("SelectedBook", "dummy.pdf")
        moveIntent.putExtra("LastPageRead", 5)
        startActivity(moveIntent)
    }

    // Gesture Function Override
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        return
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                    Utils.activateVoiceCommand(this@RiwayatActivity, REQUEST_CODE_STT)
                }
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        Utils.executeVoiceCommand(this, recognizedText.lowercase())
                    }
                }
            }
        }
    }
}