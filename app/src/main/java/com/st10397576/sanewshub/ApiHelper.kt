package com.st10397576.sanewshub

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// ApiHelper is a singleton object used to manage API setup and provide access
// to the Retrofit instance that communicates with the backend server.
object ApiHelper {
    // Base URL of the API endpoint
    const val BASE_URL = "https://sahub-api.onrender.com"

    // Lazy initialization ensures that apiService is created only when it's first needed.
    // This avoids unnecessary resource usage during app startup.
    val apiService: ApiService by lazy {
        // Create and configure the Retrofit instance
        Retrofit.Builder()
            // Define the base URL for all API requests
            .baseUrl(BASE_URL)
            // Add a converter to automatically parse JSON responses using Gson
            .addConverterFactory(GsonConverterFactory.create())
            // Build the Retrofit instance and create an implementation of the ApiService interface
            .build()
            .create(ApiService::class.java)
    }
}