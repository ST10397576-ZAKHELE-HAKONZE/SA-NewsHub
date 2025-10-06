package com.st10397576.sanewshub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LoginActivity handles user authentication.
 * It allows users to log into the SA NewsHub app using their email and password.
 * On successful login, users are redirected to the Home screen.
 */
class LoginActivity : AppCompatActivity() {
    // Declare UI components
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: androidx.appcompat.widget.AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components by finding their corresponding views in the layout
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerTextView = findViewById(R.id.textViewRegister)

        // -------------------------------
        // LOGIN BUTTON CLICK HANDLER
        // -------------------------------
        loginButton.setOnClickListener {
            // Retrieve text from the input fields
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate user input before sending request
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // If validation passes, attempt to log in
            loginUser(email, password)
        }

        // -------------------------------
        // REGISTER LINK CLICK HANDLER
        // -------------------------------
        registerTextView.setOnClickListener {
            // Navigate to RegisterActivity when user clicks "Register"
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Handles user login by sending the credentials to the API.
     * Uses coroutines to perform the network call on a background thread.
     *
     * @param email The user's email address
     * @param password The user's password
     */
    private fun loginUser(email: String, password: String) {
        // Launch a coroutine on the IO thread (for network operations)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send login request to API via Retrofit
                val response = ApiHelper.apiService.login(
                    AuthRequest(email, password)
                )
                // Switch to Main thread to update UI safely
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // If login is successful, notify user and navigate to HomeActivity
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        // Navigate to Home
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()// Close LoginActivity so user can’t go back to it
                    } else {
                        // If login fails (e.g., wrong credentials), show error
                        Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle possible exceptions (e.g., network failure, timeout)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}