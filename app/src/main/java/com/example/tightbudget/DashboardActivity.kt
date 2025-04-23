package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tightbudget.utils.ChartUtils
import com.example.tightbudget.utils.EmojiUtils
import com.example.tightbudget.utils.ProgressBarUtils

/**
 * Dashboard screen showing financial summary, goals, charts and quick access buttons.
 */
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Open ProfileActivity when the user taps the profile icon
        findViewById<FrameLayout>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupBottomNavigation()
        setupQuickActions()
        setupNavigationButtons()
        setupBudgetGoals()
        setupSpendingChart()

        // Set badge emojis using EmojiUtils
        EmojiUtils.setEmojiText(findViewById(R.id.saverBadgeIcon), EmojiUtils.getAchievementEmoji("saver"), "")
        EmojiUtils.setEmojiText(findViewById(R.id.consistentBadgeIcon), EmojiUtils.getAchievementEmoji("consistent"), "")
        EmojiUtils.setEmojiText(findViewById(R.id.transportBadgeIcon), EmojiUtils.getAchievementEmoji("transport"), "")
        EmojiUtils.setEmojiText(findViewById(R.id.lockedBadgeIcon), EmojiUtils.getAchievementEmoji("locked"), "")
    }

    /**
     * Handles bottom navigation bar.
     */
    private fun setupBottomNavigation() {
        val bottomNavBar =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavBar)
        bottomNavBar.selectedItemId = R.id.nav_dashboard

        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
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

                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Quick Actions: Add Expense, View Budget, and Goals.
     */
    private fun setupQuickActions() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        root.findViewById<LinearLayout>(R.id.addExpenseButton).setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        root.findViewById<LinearLayout>(R.id.viewBudgetButton).setOnClickListener {
            startActivity(Intent(this, BudgetGoalsActivity::class.java))
        }

        root.findViewById<LinearLayout>(R.id.goalsButton).setOnClickListener {
            startActivity(Intent(this, BudgetGoalsActivity::class.java))
        }
    }

    /**
     * Navigates to Budget Goals or Transactions screen.
     */
    private fun setupNavigationButtons() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        root.findViewById<TextView>(R.id.manageBudgetButton).setOnClickListener {
            startActivity(Intent(this, BudgetGoalsActivity::class.java))
        }

        root.findViewById<TextView>(R.id.seeAllTransactionsButton).setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        root.findViewById<TextView>(R.id.allBadgesButton)?.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }
    }

    /**
     * Sets progress bars for each category using ProgressBarUtils.
     */
    private fun setupBudgetGoals() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        ProgressBarUtils.setProgress(root.findViewById(R.id.housingProgressBar), 650.0, 800.0)
        ProgressBarUtils.setProgress(root.findViewById(R.id.foodProgressBar), 425.75, 400.0)
        ProgressBarUtils.setProgress(root.findViewById(R.id.transportProgressBar), 232.50, 250.0)
        ProgressBarUtils.setProgress(
            root.findViewById(R.id.entertainmentProgressBar),
            205.02,
            150.0
        )
    }

    /**
     * Loads spending donut chart into chart container using ChartUtils.
     */
    private fun setupSpendingChart() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        val categoryData = mapOf(
            "Housing" to 650.0,
            "Food" to 425.75,
            "Transport" to 232.50,
            "Entertainment" to 205.02
        )

        val chartContainer = root.findViewById<FrameLayout>(R.id.chartContainer)
        ChartUtils.displayDonutChart(this, chartContainer, categoryData)
    }
}