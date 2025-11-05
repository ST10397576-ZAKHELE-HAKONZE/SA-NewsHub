package com.st10397576.sanewshub

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * LoginActivity with Firebase Authentication and Google SSO.
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    // UI components
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: Button
    private lateinit var registerTextView: TextView

    // Firebase and Google Sign-In
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInHelper: GoogleSignInHelper

    // Activity result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Google Sign-In result received")
        googleSignInHelper.handleSignInResult(
            data = result.data,
            onSuccess = { account ->
                //  Using string resource with format
                Toast.makeText(
                    this,
                    getString(R.string.welcome_user, account.displayName),
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()
            },
            onFailure = { exception ->
                Log.e(TAG, "Google Sign-In failed", exception)
                // Using string resource with format
                Toast.makeText(
                    this,
                    getString(R.string.google_signin_failed, exception.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("language", "en") ?: "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d(TAG, "LoginActivity created")

        // ✅ Initialize default theme to light mode on first launch
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        if (!prefs.contains("dark_mode")) {
            // First launch - set default to light mode
            prefs.edit().putBoolean("dark_mode", false).apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            // Apply saved theme
            val isDarkMode = prefs.getBoolean("dark_mode", false)
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        googleSignInHelper = GoogleSignInHelper(this)

        // Check if user is already logged in
        if (auth.currentUser != null) {
            Log.d(TAG, "User already logged in")
            navigateToHome()
            return
        }

        // Initialize UI components
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        googleSignInButton = findViewById(R.id.buttonGoogleSignIn)
        registerTextView = findViewById(R.id.textViewRegister)

        // Email/Password Login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Already using string resource - GOOD!
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // Google Sign-In
        googleSignInButton.setOnClickListener {
            Log.d(TAG, "Google Sign-In button clicked")
            val signInIntent = googleSignInHelper.googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        // Navigate to Register
        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Handles email/password login.
     * Sends plain password to server for verification.
     */
    private fun loginUser(email: String, password: String) {
        Log.d(TAG, "Attempting login for: $email")
        loginButton.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Send plain password - server will compare with hash
                val response = ApiHelper.apiService.login(
                    AuthRequest(email, password)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, " API login successful")

                        // Also sign in with Firebase
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                Log.d(TAG, " Firebase login successful")
                                //  Using string resource
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToHome()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Firebase login failed: ${e.message}")
                                // API login succeeded, that's enough
                                // Using string resource
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToHome()
                            }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Login failed: ${response.code()} - $errorBody")

                        loginButton.isEnabled = true
                        // Using string resource
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error", e)
                withContext(Dispatchers.Main) {
                    loginButton.isEnabled = true
                    //  Using string resource with format
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.network_error, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Navigate to HomeActivity and clear back stack
     */
    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}