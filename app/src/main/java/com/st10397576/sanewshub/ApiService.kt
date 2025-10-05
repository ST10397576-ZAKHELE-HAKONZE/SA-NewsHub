package com.st10397576.sanewshub

import retrofit2.Response
import retrofit2.http.*

data class NewsItem(
    val id: Int,
    val title: String,
    val body: String,
    val timestamp: String
)

data class AuthRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val email: String? = null)

interface ApiService {
    @POST("/api/register")
    suspend fun register(@Body request: AuthRequest): Response<ApiResponse>

    @POST("/api/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse>

    @GET("/api/news")
    suspend fun getNews(): Response<List<NewsItem>>
}