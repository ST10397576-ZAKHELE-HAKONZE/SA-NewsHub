package com.st10397576.sanewshub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "settings page"
        textView.textSize = 18f
        setContentView(textView)
    }
}