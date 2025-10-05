package com.st10397576.sanewshub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val welcomeText = TextView(this).apply {
            text = "Welcome to SA NewsHub!"
            textSize = 20f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(0, 0, 0, 24)
        }

        // Mock News List
        val newsItems = listOf(
            "Load shedding Stage 4 announced for tonight",
            "Heavy rain expected in Gauteng tomorrow",
            "New job portal launched for youth graduates"
        )

        for (item in newsItems) {
            val newsItem = TextView(this).apply {
                text = "• $item"
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }
            layout.addView(newsItem)
        }

        val settingsButton = Button(this).apply {
            text = "Go to Settings"
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            }
            setPadding(0, 16, 0, 16)
        }

        layout.addView(welcomeText)
        layout.addView(settingsButton)

        setContentView(layout)
    }
}