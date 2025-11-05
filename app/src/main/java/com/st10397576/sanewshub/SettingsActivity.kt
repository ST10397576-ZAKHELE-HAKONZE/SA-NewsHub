package com.st10397576.sanewshub

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

/**
 * SettingsActivity with multi-language support.
 * Supports English, Afrikaans, and Zulu (2 SA languages as required).
 */
class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingsActivity"
    }

    private lateinit var darkModeSwitch: Switch
    private lateinit var regionSpinner: Spinner
    private lateinit var languageSpinner: Spinner

    // Flag to prevent recreate() during dark mode toggle
    private var isChangingDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize views
        darkModeSwitch = findViewById(R.id.switchDarkMode)
        regionSpinner = findViewById(R.id.spinnerRegion)
        languageSpinner = findViewById(R.id.spinnerLanguage)

        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // ===== DARK MODE =====
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        // Apply current dark mode setting
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            prefs.edit().putBoolean("dark_mode", isChecked).apply()

            // Set flag to prevent language issues
            isChangingDarkMode = true

            // Apply dark mode
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // The activity will recreate automatically - we handle language in attachBaseContext
        }

        // ===== REGION SELECTOR =====
        val regions = resources.getStringArray(R.array.regions)
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regions)
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter

        val savedRegion = prefs.getString("region", "Gauteng")
        val selectedIndex = regions.indexOf(savedRegion)
        if (selectedIndex >= 0) regionSpinner.setSelection(selectedIndex)

        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRegion = regions[position]
                prefs.edit().putString("region", selectedRegion).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ===== LANGUAGE SELECTOR =====
        val languages = resources.getStringArray(R.array.languages)
        val languageCodes = arrayOf("en", "af", "zu")

        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter

        val savedLanguage = prefs.getString("language", "en") ?: "en"
        val langIndex = languageCodes.indexOf(savedLanguage)
        if (langIndex >= 0) languageSpinner.setSelection(langIndex)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = languageCodes[position]
                val currentLanguage = prefs.getString("language", "en") ?: "en"

                // Only recreate if language actually changed
                if (selectedLanguage != currentLanguage && !isChangingDarkMode) {
                    // Save new language
                    prefs.edit().putString("language", selectedLanguage).apply()

                    // Apply locale
                    setAppLocale(selectedLanguage)

                    // Recreate activity to apply new language
                    recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    /**
     * Changes the app's language programmatically.
     */
    private fun setAppLocale(languageCode: String) {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        // Update configuration
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    /**
     * This runs BEFORE onCreate when activity is created/recreated.
     * Ensures language is always applied correctly.
     */
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", "en") ?: "en"

        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onResume() {
        super.onResume()
        // Reset flag when returning to this activity
        isChangingDarkMode = false
    }
}