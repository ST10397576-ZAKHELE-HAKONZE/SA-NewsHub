package com.st10397576.sanewshub.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase is the main Room database for the SA NewsHub app.
 * It provides access to the NewsDao for database operations.
 *
 * The database is implemented as a singleton to prevent multiple instances.
 * Version number should be incremented when schema changes are made.
 */
@Database(
    entities = [NewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to news article database operations.
     *
     * @return NewsDao instance for querying and modifying news data
     */
    abstract fun newsDao(): NewsDao

    companion object {
        // Volatile ensures that changes to INSTANCE are immediately visible to all threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton database instance.
         * Uses double-checked locking to ensure thread safety.
         *
         * @param context Application context (not activity context to prevent memory leaks)
         * @return The singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            // If INSTANCE is not null, return it; otherwise create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news_database"
                )
                    // Fallback to destructive migration if schema changes (for development)
                    // In production, you should use proper migrations
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}