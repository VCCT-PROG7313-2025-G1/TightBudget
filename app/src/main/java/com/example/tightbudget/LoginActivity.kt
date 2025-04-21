package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tightbudget.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val TAG = "LoginActivity"
    private var isRememberMeChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listeners
        setupClickListeners()

        // Log for debugging
        Log.d(TAG, "LoginActivity created")
    }

    private fun setupClickListeners() {

        // Custom checkbox click handler
        binding.checkboxView.setOnClickListener {
            toggleCheckbox()
        }

        // Login button click
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        // Sign up text click
        binding.signUpText.setOnClickListener {
            Log.d(TAG, "Sign up clicked")
            // Navigate to sign up activity
            Intent(this, SignupActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        // Forgot password click
        binding.forgotPassword.setOnClickListener {
            Log.d(TAG, "Forgot password clicked")
            // TODO: Implement forgot password functionality
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        // Social login buttons
        binding.googleLoginButton.setOnClickListener {
            Log.d(TAG, "Google login clicked")
            Toast.makeText(this, "Google login clicked", Toast.LENGTH_SHORT).show()
        }

        binding.facebookLoginButton.setOnClickListener {
            Log.d(TAG, "Facebook login clicked")
            Toast.makeText(this, "Facebook login clicked", Toast.LENGTH_SHORT).show()
        }

        binding.appleLoginButton.setOnClickListener {
            Log.d(TAG, "Apple login clicked")
            Toast.makeText(this, "Apple login clicked", Toast.LENGTH_SHORT).show()
        }

        /// Guest login
        binding.guestLoginText.setOnClickListener {
            Log.d(TAG, "Continue as guest clicked")
            // Navigate to dashboard activity
            Intent(this, DashboardActivity::class.java).also {
                startActivity(it)
                finish() // Optional: Close the current activity
            }
        }
    }

    // Function to toggle the checkbox state
    private fun toggleCheckbox() {
        isRememberMeChecked = !isRememberMeChecked
        updateCheckboxAppearance()
    }

    // Function to update the checkbox appearance based on its state
    private fun updateCheckboxAppearance() {
        if (isRememberMeChecked) {
            binding.checkboxView.setBackgroundResource(R.drawable.custom_checkbox_checked)
        } else {
            binding.checkboxView.setBackgroundResource(R.drawable.custom_checkbox)
        }
    }

    private fun performLogin() {
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val rememberMe = isRememberMeChecked

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Login attempt with username: $username, remember me: $rememberMe")

        // Here you would usually call a function to authenticate the user
        // For now, we'll just simulate a successful login
        // TODO: Implement actual login logic
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

        // Navigate to main activity
        // Intent(this, MainActivity::class.java).also {
        //     it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //     startActivity(it)
        // }
    }
}