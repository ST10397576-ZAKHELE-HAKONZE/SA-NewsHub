package com.st10397576.sanewshub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Welcome to SA NewsHub!\n(Home Feed will show news here)"
        textView.textSize = 18f
        setContentView(textView)
    }
}