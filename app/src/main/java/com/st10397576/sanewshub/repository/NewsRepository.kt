package com.st10397576.sanewshub.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.st10397576.sanewshub.ApiHelper
import com.st10397576.sanewshub.NewsItem
import com.st10397576.sanewshub.database.AppDatabase
import com.st10397576.sanewshub.database.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * NewsRepository acts as a single source of truth for news data.
 * It decides whether to fetch data from the network or local database,
 * implements offline-first architecture, and handles synchronization.
 *
 * Reference: Android Architecture Components Repository Pattern
 * https://developer.android.com/topic/architecture/data-layer
 */
class NewsRepository(context: Context) {

    private val newsDao = AppDatabase.getDatabase(context).newsDao()
    private val TAG = "NewsRepository"

    // LiveData for observing all news articles
    val allNews: LiveData<List<NewsEntity>> = newsDao.getAllNews()

    // Network status indicator
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Fetches news articles from the API and caches them locally.
     * If network fails, uses cached data (offline mode).
     *
     * This implements the offline-first pattern:
     * 1. Try to fetch from network
     * 2. If successful, update local database
     * 3. If network fails, rely on cached data
     *
     * @return Boolean indicating whether network fetch was successful
     */
    suspend fun refreshNews(): Boolean = withContext(Dispatchers.IO) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)

        try {
            Log.d(TAG, "Attempting to fetch news from API...")

            // Make API call to fetch news
            val response = ApiHelper.apiService.getNews()

            if (response.isSuccessful && response.body() != null) {
                val newsItems = response.body()!!
                Log.d(TAG, "Successfully fetched ${newsItems.size} articles from API")

                // Convert API response to database entities
                val newsEntities = newsItems.map { item ->
                    NewsEntity(
                        id = item.id,
                        title = item.title,
                        body = item.body,
                        category = item.category,
                        timestamp = item.timestamp,
                        source = item.source,
                        isSynced = true,
                        cachedAt = System.currentTimeMillis()
                    )
                }

                // Save to local database for offline access
                newsDao.insertAll(newsEntities)
                Log.d(TAG, "Articles cached successfully in local database")

                _isLoading.postValue(false)
                return@withContext true
            } else {
                // API call failed
                Log.e(TAG, "API call failed with code: ${response.code()}")
                _errorMessage.postValue("Failed to fetch news: ${response.code()}")
                _isLoading.postValue(false)
                return@withContext false
            }
        } catch (e: IOException) {
            // Network error (no internet connection)
            Log.e(TAG, "Network error: ${e.message}")
            _errorMessage.postValue("No internet connection. Showing cached articles.")
            _isLoading.postValue(false)
            return@withContext false
        } catch (e: Exception) {
            // Other errors
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            _errorMessage.postValue("Error: ${e.message}")
            _isLoading.postValue(false)
            return@withContext false
        }
    }

    /**
     * Gets the count of cached articles.
     * Useful for displaying cache status to users.
     *
     * @return Number of articles in local database
     */
    suspend fun getCachedArticleCount(): Int = withContext(Dispatchers.IO) {
        return@withContext newsDao.getNewsCount()
    }

    /**
     * Clears all cached articles from the database.
     * Should be called when user wants to clear cache or on logout.
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        Log.d(TAG, "Clearing all cached articles")
        newsDao.deleteAll()
    }

    /**
     * Deletes articles older than the specified number of days.
     * Helps manage database size and keep content fresh.
     *
     * @param days Number of days after which articles should be deleted
     */
    suspend fun deleteOldArticles(days: Int = 7) = withContext(Dispatchers.IO) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        Log.d(TAG, "Deleting articles older than $days days")
        newsDao.deleteOldArticles(cutoffTime)
    }

    /**
     * Checks if there's cached data available.
     * Useful for determining whether to show "no internet" message.
     *
     * @return True if cache has data, false otherwise
     */
    suspend fun hasCachedData(): Boolean = withContext(Dispatchers.IO) {
        return@withContext newsDao.getNewsCount() > 0
    }
}