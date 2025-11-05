package com.st10397576.sanewshub.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * NewsDao (Data Access Object) defines the database operations for news articles.
 * Room will automatically generate the implementation of these methods.
 */
@Dao
interface NewsDao {
    /**
     * Insert a list of news articles into the database.
     * If an article with the same ID exists, it will be replaced.
     *
     * @param articles List of news articles to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsEntity>)

    /**
     * Insert a single news article into the database.
     *
     * @param article The news article to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: NewsEntity)

    /**
     * Retrieve all news articles from the database.
     * Returns LiveData so the UI can observe changes automatically.
     * Articles are ordered by timestamp (newest first).
     *
     * @return LiveData list of all cached news articles
     */
    @Query("SELECT * FROM news_articles ORDER BY timestamp DESC")
    fun getAllNews(): LiveData<List<NewsEntity>>

    /**
     * Get news articles by category.
     * Useful for filtering news by topic.
     *
     * @param category The category to filter by
     * @return LiveData list of articles in the specified category
     */
    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY timestamp DESC")
    fun getNewsByCategory(category: String): LiveData<List<NewsEntity>>

    /**
     * Delete all news articles from the database.
     * Useful for clearing cache or forcing a fresh sync.
     */
    @Query("DELETE FROM news_articles")
    suspend fun deleteAll()

    /**
     * Get the total count of cached articles.
     *
     * @return Number of articles in the database
     */
    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getNewsCount(): Int

    /**
     * Delete old articles that were cached more than X days ago.
     * This helps manage database size and keeps content fresh.
     *
     * @param cutoffTime Timestamp before which articles should be deleted
     */
    @Query("DELETE FROM news_articles WHERE cachedAt < :cutoffTime")
    suspend fun deleteOldArticles(cutoffTime: Long)
}