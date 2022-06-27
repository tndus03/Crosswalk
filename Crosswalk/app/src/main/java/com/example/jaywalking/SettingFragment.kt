package com.example.jaywalking

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import androidx.preference.*


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val idPreference: EditTextPreference? = findPreference("id")
        idPreference?.title = "ID 변경"
        idPreference?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text)) {
                    "별명이 설정되지 않았습니다."
                } else {
                    "설정된 별명은 $text 입니다."
                }
            }

        val colorPreference: ListPreference? = findPreference("color")
        colorPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val notificationPreference: ListPreference? = findPreference("noti")
        notificationPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()

        val themePreference: ListPreference ?= findPreference(getString(R.string.font))
        themePreference?.onPreferenceChangeListener = modeChangeListener
        themePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }

    private val modeChangeListener = object : Preference.OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            newValue as? String
            when (newValue) {
                getString(R.string.tway_air) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                }
                getString(R.string.myyeongnamnu) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            return true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}