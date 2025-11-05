package com.st10397576.sanewshub

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * RegisterActivity handles user registration.
 */
class RegisterActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", "en") ?: "en"
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Log.d(TAG, "RegisterActivity created")
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Using string resource
                Toast.makeText(
                    this,
                    getString(R.string.fill_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                // Using string resource
                Toast.makeText(
                    this,
                    getString(R.string.password_length),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            registerUser(email, password)
        }
    }

    /**
     * Registers user with backend API.
     * Sends plain password - server will hash it.
     */
    private fun registerUser(email: String, password: String) {
        Log.d(TAG, "Starting registration for: $email")
        registerButton.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send plain password - server will hash it
                val response = ApiHelper.apiService.register(
                    AuthRequest(email, password)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "API registration successful")

                        // Also register with Firebase
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                Log.d(TAG, "Firebase registration successful")
                                // Using string resource
                                Toast.makeText(
                                    this@RegisterActivity,
                                    getString(R.string.register_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Firebase registration failed: ${e.message}")
                                // Using string resource
                                Toast.makeText(
                                    this@RegisterActivity,
                                    getString(R.string.register_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Registration failed: ${response.code()} - $errorBody")

                        registerButton.isEnabled = true

                        // Using string resource (need to add this one!)
                        val errorMessage = if (errorBody?.contains("already exists") == true) {
                            getString(R.string.email_exists)
                        } else {
                            getString(R.string.registration_failed)
                        }

                        Toast.makeText(
                            this@RegisterActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error", e)
                withContext(Dispatchers.Main) {
                    registerButton.isEnabled = true
                    // Using string resource with format
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.network_error, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}