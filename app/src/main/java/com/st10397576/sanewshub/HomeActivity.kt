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

/**
 * HomeActivity serves as the main landing page of the SA NewsHub app.
 * It dynamically loads and displays news articles retrieved from the API.
 * The activity is built programmatically (without XML layout) using a LinearLayout as the root view.
 */
class HomeActivity : AppCompatActivity() {
    // Root layout for the activity; all UI components will be added here programmatically
    private lateinit var rootLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize a vertical LinearLayout to hold all the UI components
        rootLayout = LinearLayout(this).apply {
            id = R.id.home_root
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // The layout will be populated dynamically, so we set it as the main content view
        setContentView(rootLayout)
        // Fetch and display news articles from the API
        fetchNews()
    }

    /**
     * Displays an error message on the screen when news loading fails.
     * Also re-adds a "Go to Settings" button for navigation.
     */
    private fun showError(message: String) {
        // Clear previous content except welcome & button
        rootLayout.removeAllViews()

        // Create a TextView to show the error message in red
        val errorText = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(getColor(android.R.color.holo_red_dark))
            setPadding(0, 8, 0, 8)
        }

        rootLayout.addView(errorText)

        // Add a button allowing users to navigate to Settings
        rootLayout.addView(Button(this).apply {
            text = "Go to Settings"
            setOnClickListener {
                startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            }
        })
    }

    /**
     * Fetches news articles from the backend API asynchronously using Kotlin coroutines.
     * Updates the UI based on the success or failure of the network request.
     */
    private fun fetchNews() {
        // Launch a coroutine on a background thread for network operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Perform the API request to fetch news articles
                val response = ApiHelper.apiService.getNews()
                // Switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    rootLayout.removeAllViews() // Clear loading

                    // Add a welcome title at the top
                    val welcome = TextView(this@HomeActivity).apply {
                        text = "Welcome to SA NewsHub!"
                        textSize = 20f
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        setPadding(0, 0, 0, 24)
                    }
                    rootLayout.addView(welcome)

                    // If the request is successful and contains news data
                    if (response.isSuccessful && response.body() != null) {
                        // Iterate through each news item and display its title
                        for (item in response.body()!!) {
                            val newsItem = TextView(this@HomeActivity).apply {
                                text = "• ${item.title}"
                                textSize = 16f
                                setPadding(0, 8, 0, 8)
                            }
                            rootLayout.addView(newsItem)
                        }
                    } else {
                        // Show an error message if the response fails or is empty
                        showError("Failed to load news")
                    }

                    // Re-add the "Go to Settings" button at the bottom of the screen
                    rootLayout.addView(Button(this@HomeActivity).apply {
                        text = "Go to Settings"
                        setOnClickListener {
                            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
                        }
                        setPadding(0, 16, 0, 16)
                    })
                }
            } catch (e: Exception) {
                // Handle any network or unexpected exceptions
                withContext(Dispatchers.Main) {
                    showError("Network error: ${e.message}")
                }
            }
        }
    }
}