package com.st10397576.sanewshub.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * NewsEntity represents a cached news article stored in the local Room database.
 * This allows the app to display news even when offline.
 *
 * @property id Unique identifier for the news article (from API)
 * @property title Headline of the news article
 * @property body Full text content of the article
 * @property category Category/topic of the news (e.g., Politics, Sports)
 * @property timestamp When the article was published
 * @property source News source/publisher
 * @property isSynced Whether this article has been synced from the server
 * @property cachedAt When this article was cached locally
 */
@Entity(tableName = "news_articles")
data class NewsEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String,
    val category: String,
    val timestamp: String,
    val source: String,
    val isSynced: Boolean = true, // True if from server, false if created offline
    val cachedAt: Long = System.currentTimeMillis() // When cached locally
)