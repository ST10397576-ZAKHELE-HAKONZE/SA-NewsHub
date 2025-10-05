package com.st10397576.sanewshub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var rootLayout: LinearLayout // 👈 Declare reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootLayout = LinearLayout(this).apply {
            id = R.id.home_root
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // ... (add welcomeText, loadingText, settingsButton to rootLayout)

        setContentView(rootLayout)
        fetchNews()
    }

    private fun showError(message: String) {
        // Clear previous content except welcome & button
        rootLayout.removeAllViews()

        val errorText = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(getColor(android.R.color.holo_red_dark))
            setPadding(0, 8, 0, 8)
        }

        rootLayout.addView(errorText)

        // Re-add settings button
        rootLayout.addView(Button(this).apply {
            text = "Go to Settings"
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            }
        })
    }

    // Similarly update fetchNews() to use rootLayout instead of findViewById
    private fun fetchNews() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiHelper.apiService.getNews()
                withContext(Dispatchers.Main) {
                    rootLayout.removeAllViews() // Clear loading

                    val welcome = TextView(this@HomeActivity).apply {
                        text = "Welcome to SA NewsHub!"
                        textSize = 20f
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        setPadding(0, 0, 0, 24)
                    }
                    rootLayout.addView(welcome)

                    if (response.isSuccessful && response.body() != null) {
                        for (item in response.body()!!) {
                            val newsItem = TextView(this@HomeActivity).apply {
                                text = "• ${item.title}"
                                textSize = 16f
                                setPadding(0, 8, 0, 8)
                            }
                            rootLayout.addView(newsItem)
                        }
                    } else {
                        showError("Failed to load news")
                    }

                    // Always re-add settings button
                    rootLayout.addView(Button(this@HomeActivity).apply {
                        text = "Go to Settings"
                        setOnClickListener {
                            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
                        }
                        setPadding(0, 16, 0, 16)
                    })
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Network error: ${e.message}")
                }
            }
        }
    }
}