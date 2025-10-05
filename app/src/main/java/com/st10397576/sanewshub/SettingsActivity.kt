package com.st10397576.sanewshub

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: Switch
    private lateinit var regionSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        darkModeSwitch = findViewById(R.id.switchDarkMode)
        regionSpinner = findViewById(R.id.spinnerRegion)

        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val savedRegion = prefs.getString("region", "Gauteng")

        darkModeSwitch.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val regions = arrayOf("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Limpopo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = adapter

        val selectedIndex = regions.indexOf(savedRegion)
        if (selectedIndex >= 0) regionSpinner.setSelection(selectedIndex)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit()
                .putBoolean("dark_mode", isChecked)
                .apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = regions[position]
                prefs.edit()
                    .putString("region", selected)
                    .apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Required
            }
        }
    }
}