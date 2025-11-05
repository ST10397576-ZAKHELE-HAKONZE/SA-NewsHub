package com.st10397576.sanewshub

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.st10397576.sanewshub.database.NewsEntity
import com.st10397576.sanewshub.repository.NewsRepository
import kotlinx.coroutines.launch
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Context
import android.content.res.Configuration
import java.util.*

/**
 * HomeActivity serves as the main landing page of the SA NewsHub app.
 * It displays news articles using RecyclerView and supports offline mode.
 *
 * Features:
 * - Fetches news from API when online
 * - Displays cached news when offline
 * - Automatic refresh on app start
 * - Pull-to-refresh functionality (can be added with SwipeRefreshLayout)
 *
 * Reference: Android Offline-First Architecture
 * https://developer.android.com/topic/architecture/data-layer
 */
class HomeActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var newsRepository: NewsRepository
    private lateinit var newsAdapter: NewsAdapter

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", "en") ?: "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "HomeActivity created")

        // Apply saved theme
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        if (!prefs.contains("dark_mode")) {
            prefs.edit().putBoolean("dark_mode", false).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            val isDarkMode = prefs.getBoolean("dark_mode", false)
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.home_title)

        newsRepository = NewsRepository(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(emptyList())
        recyclerView.adapter = newsAdapter

        newsRepository.allNews.observe(this) { articles ->
            Log.d(TAG, "News data updated: ${articles.size} articles")
            updateUI(articles)
        }

        newsRepository.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                Log.d(TAG, "Loading news...")
            } else {
                Log.d(TAG, "Finished loading")
            }
        }

        newsRepository.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $it")
            }
        }

        // Only auto-fetch on first open (not on recreate)
        if (savedInstanceState == null) {
            refreshNews(showSuccessToast = false) // Auto-fetch without toast
        }
    }

    /**
     * Creates the options menu in the toolbar.
     * Adds Settings and Refresh menu items.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    /**
     * Handles menu item clicks.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                // Manual refresh WILL show toast
                refreshNews(showSuccessToast = true)
                true
            }
            R.id.action_clear_cache -> {
                clearCache()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Refreshes news from the API.
     * If offline, will display cached data automatically via LiveData.
     */
    private fun refreshNews(showSuccessToast: Boolean = true) {
        Log.d(TAG, "Refreshing news...")

        lifecycleScope.launch {
            val success = newsRepository.refreshNews()

            if (success) {
                // Only show toast if requested
                if (showSuccessToast) {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.news_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d(TAG, "News refreshed successfully")
            } else {
                val hasCached = newsRepository.hasCachedData()
                if (hasCached) {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.showing_cached),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.no_cache),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Clears all cached articles from the database.
     */
    private fun clearCache() {
        lifecycleScope.launch {
            newsRepository.clearCache()
            Toast.makeText(
                this@HomeActivity,
                "Cache cleared",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "Cache cleared by user")
        }
    }

    /**
     * Updates the RecyclerView with new data.
     *
     * @param articles List of news articles to display
     */
    private fun updateUI(articles: List<NewsEntity>) {
        if (articles.isEmpty()) {
            Log.d(TAG, "No articles to display")
            // You can show an empty state view here
        } else {
            Log.d(TAG, "Displaying ${articles.size} articles")
        }

        // Convert NewsEntity to NewsItem for the adapter
        val newsItems = articles.map { entity ->
            NewsItem(
                id = entity.id,
                title = entity.title,
                body = entity.body,
                category = entity.category,
                timestamp = entity.timestamp,
                source = entity.source
            )
        }

        // Update adapter with new data
        newsAdapter = NewsAdapter(newsItems)
        recyclerView.adapter = newsAdapter
    }
}