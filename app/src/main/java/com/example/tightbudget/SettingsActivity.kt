package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tightbudget.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * SettingsActivity displays the Settings screen for the app.
 * It provides navigation to other areas of the app and placeholders
 * for future features like profile editing, theme switching, and support.
 */
class SettingsActivity : AppCompatActivity() {

    // View binding for accessing layout views directly
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise the bottom navigation bar and handle tab clicks
        setupBottomNavigation()

        // Handle click events for each settings item
        setupClickListeners()

        setupBackButton()

    }

    /**
     * Handles bottom navigation bar.
     */
    private fun setupBottomNavigation() {
        val bottomNavBar = binding.bottomNavBar
        bottomNavBar.selectedItemId = R.id.nav_settings

        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_reports -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_add_transaction -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_wallet -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_settings -> true // Already on this screen

                else -> false
            }
        }
    }

    // Handles the back button click
    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * This method sets up click listeners for all the setting options.
     * Each click currently logs a message; functionality can be added later.
     */
    private fun setupClickListeners() {
        binding.editProfile.setOnClickListener {
            Log.d("Settings", "Edit Profile clicked")
            // TODO: Navigate to Edit Profile screen when ready
        }

        binding.changePassword.setOnClickListener {
            Log.d("Settings", "Change Password clicked")
            // TODO: Navigate to Change Password screen
        }

        binding.goalReminders.setOnClickListener {
            Log.d("Settings", "Goal Reminders clicked")
            // TODO: Toggle notifications or navigate to reminder settings
        }

        binding.darkMode.setOnClickListener {
            Log.d("Settings", "Dark Mode clicked")
            // TODO: Implement a theme switch (light <-> dark)
        }

        binding.help.setOnClickListener {
            Log.d("Settings", "Help clicked")
            // TODO: Navigate to Help or FAQ section
        }

        binding.feedback.setOnClickListener {
            Log.d("Settings", "Feedback clicked")
            // TODO: Launch a feedback form or open mail client
        }

        binding.terms.setOnClickListener {
            Log.d("Settings", "Terms clicked")
            // TODO: Display Terms & Conditions screen or dialog
        }

        binding.privacy.setOnClickListener {
            Log.d("Settings", "Privacy clicked")
            // TODO: Display Privacy Policy screen or dialog
        }

        binding.signOut.setOnClickListener {
            Log.d("Settings", "Sign out clicked")
            // TODO: Implement logout flow and return to login screen
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}