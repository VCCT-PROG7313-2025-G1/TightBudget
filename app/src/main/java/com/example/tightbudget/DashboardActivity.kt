package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import com.example.tightbudget.data.Category
import com.example.tightbudget.utils.DrawableUtils
import com.example.tightbudget.utils.EmojiUtils
import com.example.tightbudget.utils.ProgressBarUtils
import com.example.tightbudget.utils.ChartUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.tightbudget.base.BaseActivity

class DashboardActivity : BaseActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        val headerFrame = findViewById<FrameLayout>(R.id.headerFrame)
        bottomNavigation = findViewById(R.id.bottomNavBar)

        // For API level 24 compatibility
        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            // Handle status bar (top) insets
            headerFrame.setPadding(0, insets.systemWindowInsetTop, 0, 0)

            // Handle navigation bar (bottom) insets
            bottomNavigation.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }

        // Set up bottom navigation
        setupBottomNavigation()

        // Set up circle backgrounds for various UI elements
        setupCircleBackgrounds()

        // Set up category indicators with appropriate colours
        setupCategoryIndicators()

        // Set up transaction icons with appropriate emojis
        setupTransactionIcons()

        // Set up quick action buttons
        setupQuickActionButtons()

        // Set up achievement badges
        setupAchievementBadges()

        // Set up progress bars for budget goals
        setupBudgetProgressBars()

        // Set up donut chart
        setupDonutChart()
    }

    private fun setupBottomNavigation() {
        Log.d("TightBudget", "Setting up bottom navigation")

        // Set dashboard as selected by default
        bottomNavigation.selectedItemId = R.id.nav_dashboard

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    Log.d("TightBudget", "Dashboard tab selected")
                    true
                }
                R.id.nav_reports -> {
                    Log.d("TightBudget", "Reports tab selected")
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    true
                }
                R.id.nav_wallet -> {
                    Log.d("TightBudget", "Transactions tab selected")
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    Log.d("TightBudget", "Settings tab selected")
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.nav_add_transaction -> {
                    Log.d("TightBudget", "Add Transaction tab selected")
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupCircleBackgrounds() {
        val profileButton = findViewById<FrameLayout>(R.id.profileButton).getChildAt(0) as TextView
        DrawableUtils.applyWhiteCircleBackground(profileButton, this)

        val notificationBadge = findViewById<FrameLayout>(R.id.profileButton).getChildAt(1) as TextView
        notificationBadge.background = DrawableUtils.createYellowCircle(this)

        val transaction1Icon = findViewById<TextView>(R.id.transaction1Icon)
        val transaction2Icon = findViewById<TextView>(R.id.transaction2Icon)
        val transaction3Icon = findViewById<TextView>(R.id.transaction3Icon)
        val transaction4Icon = findViewById<TextView>(R.id.transaction4Icon)

        DrawableUtils.applyLightGrayCircleBackground(transaction1Icon, this)
        DrawableUtils.applyLightGrayCircleBackground(transaction2Icon, this)
        DrawableUtils.applyLightGrayCircleBackground(transaction3Icon, this)
        DrawableUtils.applyLightGrayCircleBackground(transaction4Icon, this)

        val saverBadgeIcon = findViewById<TextView>(R.id.saverBadgeIcon)
        val consistentBadgeIcon = findViewById<TextView>(R.id.consistentBadgeIcon)
        val transportBadgeIcon = findViewById<TextView>(R.id.transportBadgeIcon)
        val lockedBadgeIcon = findViewById<TextView>(R.id.lockedBadgeIcon)

        DrawableUtils.applyLightGrayCircleBackground(saverBadgeIcon, this)
        DrawableUtils.applyLightGrayCircleBackground(consistentBadgeIcon, this)
        DrawableUtils.applyLightGrayCircleBackground(transportBadgeIcon, this)
        DrawableUtils.applyLightGrayCircleBackground(lockedBadgeIcon, this)
    }

    private fun setupCategoryIndicators() {
        findViewById<View>(R.id.housingIndicator).background =
            DrawableUtils.createCircle(DrawableUtils.getCategoryColor(this, Category.HOUSING))

        findViewById<View>(R.id.foodIndicator).background =
            DrawableUtils.createCircle(DrawableUtils.getCategoryColor(this, Category.FOOD))

        findViewById<View>(R.id.transportIndicator).background =
            DrawableUtils.createCircle(DrawableUtils.getCategoryColor(this, Category.TRANSPORT))

        findViewById<View>(R.id.entertainmentIndicator).background =
            DrawableUtils.createCircle(DrawableUtils.getCategoryColor(this, Category.ENTERTAINMENT))
    }

    private fun setupTransactionIcons() {
        findViewById<TextView>(R.id.transaction1Icon).text = EmojiUtils.getTransactionEmoji("shopping")
        findViewById<TextView>(R.id.transaction2Icon).text = EmojiUtils.getTransactionEmoji("fuel")
        findViewById<TextView>(R.id.transaction3Icon).text = EmojiUtils.getTransactionEmoji("nando's")
        findViewById<TextView>(R.id.transaction4Icon).text = EmojiUtils.getTransactionEmoji("salary")
    }

    private fun setupQuickActionButtons() {
        findViewById<TextView>(R.id.addExpenseIcon).text = EmojiUtils.getActionEmoji("expense")
        findViewById<TextView>(R.id.viewBudgetIcon).text = EmojiUtils.getActionEmoji("budget")
        findViewById<TextView>(R.id.goalsIcon).text = EmojiUtils.getActionEmoji("goals")
    }

    private fun setupAchievementBadges() {
        findViewById<TextView>(R.id.saverBadgeIcon).text = EmojiUtils.getAchievementEmoji("saver")
        findViewById<TextView>(R.id.consistentBadgeIcon).text = EmojiUtils.getAchievementEmoji("consistent")
        findViewById<TextView>(R.id.transportBadgeIcon).text = EmojiUtils.getAchievementEmoji("transport")
        findViewById<TextView>(R.id.lockedBadgeIcon).text = EmojiUtils.getAchievementEmoji("locked")
    }

    private fun setupBudgetProgressBars() {
        val housingProgressBar = findViewById<ProgressBar>(R.id.housingProgressBar)
        val foodProgressBar = findViewById<ProgressBar>(R.id.foodProgressBar)
        val transportProgressBar = findViewById<ProgressBar>(R.id.transportProgressBar)
        val entertainmentProgressBar = findViewById<ProgressBar>(R.id.entertainmentProgressBar)

        ProgressBarUtils.applyCategoryProgressBar(housingProgressBar, this, Category.HOUSING)
        ProgressBarUtils.applyCategoryProgressBar(foodProgressBar, this, Category.FOOD)
        ProgressBarUtils.applyCategoryProgressBar(transportProgressBar, this, Category.TRANSPORT)
        ProgressBarUtils.applyCategoryProgressBar(entertainmentProgressBar, this, Category.ENTERTAINMENT)

        housingProgressBar.progress = 81
        foodProgressBar.progress = 106
        transportProgressBar.progress = 93
        entertainmentProgressBar.progress = 137
    }

    private fun setupDonutChart() {
        ChartUtils.addDonutChartToContainer(this, R.id.chartContainer, this)
    }
}
