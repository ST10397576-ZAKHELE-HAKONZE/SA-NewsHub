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

        // Create a vertical layout to hold both text and button
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48) // left, top, right, bottom (in pixels)
        }

        // Welcome text
        val textView = TextView(this).apply {
            text = "Welcome to SA NewsHub!\n(Home Feed will show news here)"
            textSize = 18f
            setPadding(0, 0, 0, 32) // space below text
        }

        // Settings button
        val button = Button(this).apply {
            text = "Go to Settings"
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            }
        }

        // Add views to layout
        layout.addView(textView)
        layout.addView(button)

        // Set the layout as content
        setContentView(layout)
    }
}