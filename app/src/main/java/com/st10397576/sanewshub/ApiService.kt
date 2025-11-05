package com.st10397576.sanewshub

import retrofit2.Response
import retrofit2.http.*

// ---------------------------
// Data Models
// ---------------------------
data class NewsItem(
    val id: Int,
    val title: String,
    val body: String,
    val category: String,
    val timestamp: String,
    val source: String
)

// Represents the request body for authentication-related actions (register/login)
data class AuthRequest(val email: String, val password: String)
// Represents a generic response from the API (used for register/login responses)
data class ApiResponse(val message: String, val email: String? = null)
// Request body for registering FCM token
data class FcmTokenRequest(val email: String, val fcmToken: String)

// ---------------------------
// API Service Interface
// ---------------------------

// ApiService defines the endpoints for communicating with the backend API using Retrofit.
// Each function corresponds to a specific HTTP request
interface ApiService {
    // Sends a POST request to register a new user.
    // The AuthRequest object is sent in the request body.
    @POST("/api/register")
    suspend fun register(@Body request: AuthRequest): Response<ApiResponse>

    // Sends a POST request to log in an existing user.
    // The server validates the email and password provided in the AuthRequest.
    @POST("/api/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse>

    // Sends a GET request to retrieve a list of news items.
    // The response body will contain a list of NewsItem objects.
    @GET("/api/news")
    suspend fun getNews(): Response<List<NewsItem>>
}