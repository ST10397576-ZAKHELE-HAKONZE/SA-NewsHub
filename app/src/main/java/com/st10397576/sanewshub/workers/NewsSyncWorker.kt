package com.st10397576.sanewshub.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.st10397576.sanewshub.repository.NewsRepository

/**
 * NewsSyncWorker handles background synchronization of news articles.
 * It runs when the device regains internet connectivity to fetch latest news.
 *
 * WorkManager ensures this task runs even if the app is closed,
 * and respects device constraints (battery, network).
 *
 * Reference: WorkManager Background Work
 * https://developer.android.com/topic/libraries/architecture/workmanager
 */
class NewsSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "NewsSyncWorker"

    /**
     * Performs the background sync operation.
     * This method runs on a background thread automatically.
     *
     * @return Result.success() if sync completed, Result.retry() if failed
     */
    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting background sync...")

        return try {
            // Initialize repository
            val repository = NewsRepository(applicationContext)

            // Attempt to fetch latest news
            val success = repository.refreshNews()

            if (success) {
                Log.d(TAG, "Background sync completed successfully")

                // Clean up old articles (older than 7 days)
                repository.deleteOldArticles(days = 7)

                Result.success()
            } else {
                Log.w(TAG, "Background sync failed, will retry")
                // Retry the work if it failed
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during background sync: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        // Unique work name for periodic sync
        const val WORK_NAME = "news_sync_work"
    }
}