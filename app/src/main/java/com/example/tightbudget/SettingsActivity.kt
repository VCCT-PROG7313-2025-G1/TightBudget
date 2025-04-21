package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

        // Fix header cut-off on devices with status bar / notch
        ViewCompat.setOnApplyWindowInsetsListener(binding.header) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.updatePadding(top = topInset + view.paddingTop)
            insets
        }

        // Initialise the bottom navigation bar and handle tab clicks
        setupBottomNavigation()

        // Handle click events for each settings item
        setupClickListeners()
    }

    /**
     * This method configures the bottom navigation bar.
     * It highlights the "Settings" tab and defines actions for each tab.
     */
    private fun setupBottomNavigation() {
        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        navBar.selectedItemId = R.id.nav_settings

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // Navigate to the dashboard screen
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_wallet -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_reports -> {
                    // Placeholder: add ReportsActivity if required
                    true
                }
                R.id.nav_add_transaction -> {
                    // Navigate to the Add Transaction screen
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.nav_settings -> true // Already on this screen
                else -> false
            }
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
}