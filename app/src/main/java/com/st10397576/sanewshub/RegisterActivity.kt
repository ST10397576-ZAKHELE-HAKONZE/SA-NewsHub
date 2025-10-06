package com.st10397576.sanewshub

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
 * RegisterActivity handles user registration for the SA NewsHub app.
 * It allows new users to create an account by providing an email and password.
 * The registration request is sent to the backend API using Retrofit and Kotlin coroutines.
 */
class RegisterActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI elements by connecting them to their IDs in the XML layout
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.buttonRegister)

        // -------------------------------
        // REGISTER BUTTON CLICK HANDLER
        // -------------------------------
        registerButton.setOnClickListener {
            // Retrieve text from input fields and remove any extra spaces
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            // Validate that both fields are filled in before proceeding
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // If validation passes, call function to register the user
            registerUser(email, password)
        }
    }

    /**
     * Sends a registration request to the backend API using Retrofit.
     * Executes the network call asynchronously using Kotlin coroutines.
     *
     * @param email The user's email address
     * @param password The user's chosen password
     */
    private fun registerUser(email: String, password: String) {
        // Launch a coroutine on a background (IO) thread for the network operation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the register endpoint defined in ApiService
                val response = ApiHelper.apiService.register(
                    AuthRequest(email, password)
                )
                // Switch to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // If registration is successful, show a success message
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        finish() // Go back to Login
                    } else {
                        // If registration fails (e.g., email already exists), show error message
                        Toast.makeText(this@RegisterActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle network errors or exceptions gracefully
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}