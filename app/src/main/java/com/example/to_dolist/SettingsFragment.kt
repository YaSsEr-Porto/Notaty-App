package com.example.to_dolist

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment() : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val themePreference = findPreference<ListPreference>("theme")
        themePreference?.setOnPreferenceChangeListener { preference, newValue ->
            saveThemePreference(newValue.toString())
            true
        }
    }

    fun saveThemePreference(theme: String) {

        val sharedPreferences = requireActivity().getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("theme", theme).apply()

        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        val activity = requireActivity()
        activity.window.setWindowAnimations(R.style.ThemeTransition)
        activity.recreate()
    }
}