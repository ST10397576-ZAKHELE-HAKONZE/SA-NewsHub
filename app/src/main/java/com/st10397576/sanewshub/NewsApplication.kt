package com.st10397576.sanewshub

import android.app.Application
import android.util.Log
import androidx.work.*
import com.st10397576.sanewshub.workers.NewsSyncWorker
import java.util.concurrent.TimeUnit

/**
 * NewsApplication is the custom Application class for SA NewsHub.
 * It initializes app-wide components like WorkManager for background sync.
 *
 * This class is instantiated before any activity when the app starts.
 * Perfect place for one-time initialization tasks.
 */
class NewsApplication : Application() {

    private val TAG = "NewsApplication"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NewsApplication initialized")

        // Set up periodic background sync
        setupPeriodicSync()
    }

    /**
     * Configures WorkManager to periodically sync news in the background.
     * Sync runs every 6 hours when device has internet connection.
     *
     * This ensures users always have fresh content when they open the app,
     * even if they've been offline for a while.
     */
    private fun setupPeriodicSync() {
        // Define constraints for when the work should run
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when internet is available
            .setRequiresBatteryNotLow(true) // Don't drain battery
            .build()

        // Create a periodic work request that runs every 6 hours
        val syncWorkRequest = PeriodicWorkRequestBuilder<NewsSyncWorker>(
            6, TimeUnit.HOURS, // Repeat interval
            15, TimeUnit.MINUTES // Flex interval (can run within 15 min window)
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Enqueue the work (replacing any existing work with same name)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NewsSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
            syncWorkRequest
        )

        Log.d(TAG, "Periodic sync scheduled (every 6 hours)")
    }
}