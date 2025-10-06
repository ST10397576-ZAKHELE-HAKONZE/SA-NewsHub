package com.st10397576.sanewshub

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * SettingsActivity allows users to customize app preferences such as:
 * - Enabling/disabling Dark Mode
 * - Selecting their preferred region
 *
 * The user's preferences are stored using SharedPreferences so that settings
 * persist between app sessions.
 */
class SettingsActivity : AppCompatActivity() {
    // UI components
    private lateinit var darkModeSwitch: Switch
    private lateinit var regionSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        // Initialize views by linking them to layout components
        darkModeSwitch = findViewById(R.id.switchDarkMode)
        regionSpinner = findViewById(R.id.spinnerRegion)

        // Initialize views by linking them to layout components
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        // Retrieve previously saved settings (if any)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val savedRegion = prefs.getString("region", "Gauteng")

        // Apply the saved Dark Mode setting
        darkModeSwitch.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // -------------------------------
        // REGION SPINNER SETUP
        // -------------------------------
        val regions = arrayOf("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Limpopo")
        // Create an adapter to display the regions in a dropdown list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = adapter
        // Restore the previously selected region, if available
        val selectedIndex = regions.indexOf(savedRegion)
        if (selectedIndex >= 0) regionSpinner.setSelection(selectedIndex)
        // Listen for changes in the Dark Mode switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the new Dark Mode state in SharedPreferences
            prefs.edit()
                .putBoolean("dark_mode", isChecked)
                .apply()
            // Apply the selected theme immediately
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        // -------------------------------
        // REGION SELECTION LISTENER
        // -------------------------------

        // Listen for user selection from the region dropdown
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = regions[position]
                prefs.edit()
                    .putString("region", selected)
                    .apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Required override, no action needed if no item is selected
            }
        }
    }
}