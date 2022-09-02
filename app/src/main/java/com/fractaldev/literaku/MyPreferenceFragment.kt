package com.fractaldev.literaku

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class MyPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var SPEED_SPEECH: String

    private lateinit var speedSpeechPreference: ListPreference

    companion object {
        private const val DEFAULT_VALUE = "1"
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        init()
        setSummaries()
    }

    private fun init() {
        SPEED_SPEECH = resources.getString(R.string.KEY_SPEED_SPEECH)
        speedSpeechPreference = findPreference<ListPreference> (SPEED_SPEECH) as ListPreference
    }

    private fun setSummaries() {
        val sh = preferenceManager.sharedPreferences

        setSummariesSpeedSpeech(sh)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null) {
            if (key == SPEED_SPEECH) {
                setSummariesSpeedSpeech(sharedPreferences)
            }
        }
    }

    private fun setSummariesSpeedSpeech(sharedPreferences: SharedPreferences?) {
        if (sharedPreferences != null) {
            var currentSpeechSpeed = sharedPreferences.getString(SPEED_SPEECH, DEFAULT_VALUE)
            var currentSpeechSpeedDesc = when (currentSpeechSpeed) {
                "0.3F" -> "Sangat Lambat"
                "0.65F" -> "Lambat"
                "1F" -> "Normal"
                "1.35F" -> "Cepat"
                "1.7F" -> "Sangat Cepat"
                else -> ""
            }
            speedSpeechPreference.summary = currentSpeechSpeedDesc
        }
    }
}