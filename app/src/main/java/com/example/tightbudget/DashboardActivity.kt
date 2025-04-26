package com.example.tightbudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tightbudget.adapters.TransactionAdapter
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.data.Category
import com.example.tightbudget.databinding.ActivityDashboardBinding
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.ui.TransactionDetailBottomSheet
import com.example.tightbudget.utils.ChartUtils
import com.example.tightbudget.utils.DrawableUtils
import com.example.tightbudget.utils.EmojiUtils
import com.example.tightbudget.utils.ProgressBarUtils
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Dashboard screen showing financial summary, goals, charts and quick access buttons.
 */
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Find the TextViews manually since they are inside included layouts
        val welcomeTextView = findViewById<TextView>(R.id.welcomeText)
        val balanceAmountView = findViewById<TextView>(R.id.balanceAmount)

        // Initialise the database
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // Get the email from the intent
        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (!userEmail.isNullOrEmpty()) {
            lifecycleScope.launch {
                val user = userDao.getUserByEmail(userEmail)

                if (user != null) {
                    // Set welcome message and balance
                    welcomeTextView.text = "Welcome back, ${user.fullName}!"
                    balanceAmountView.text = "R%.2f".format(user.balance)
                } else {
                    Log.e("DashboardActivity", "User not found in database for email: $userEmail")
                    Toast.makeText(this@DashboardActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Open ProfileActivity when the user taps the profile icon
        findViewById<FrameLayout>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupBottomNavigation()
        setupQuickActions()
        setupNavigationButtons()
        setupBudgetGoals()
        setupSpendingChart()
        setupAchievementBadges()
        setupRecentTransactions()
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

        // Populate spending legend
        populateSpendingLegend()
    }

    /**
     * Sets badge emojis and background styles.
     */
    private fun setupAchievementBadges() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Saver
        val saverBadge = root.findViewById<TextView>(R.id.saverBadgeIcon)
        EmojiUtils.setEmojiText(saverBadge, EmojiUtils.getAchievementEmoji("saver"), "")
        DrawableUtils.applyCircleBackground(saverBadge, getColor(R.color.green_light))

        // Consistent
        val consistentBadge = root.findViewById<TextView>(R.id.consistentBadgeIcon)
        EmojiUtils.setEmojiText(consistentBadge, EmojiUtils.getAchievementEmoji("consistent"), "")
        DrawableUtils.applyCircleBackground(consistentBadge, getColor(R.color.orange))

        // Transport
        val transportBadge = root.findViewById<TextView>(R.id.transportBadgeIcon)
        EmojiUtils.setEmojiText(transportBadge, EmojiUtils.getAchievementEmoji("transport"), "")
        DrawableUtils.applyCircleBackground(transportBadge, getColor(R.color.blue_light))

        // Locked
        val lockedBadge = root.findViewById<TextView>(R.id.lockedBadgeIcon)
        EmojiUtils.setEmojiText(lockedBadge, EmojiUtils.getAchievementEmoji("locked"), "")
        lockedBadge.alpha = 0.5f
        lockedBadge.background = DrawableUtils.createCircleOutline(
            strokeColor = getColor(R.color.gray_medium),
            strokeWidth = 3,
            fillColor = getColor(R.color.background_gray)
        )
    }

    private fun populateSpendingLegend() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)
        val legendContainer = root.findViewById<LinearLayout>(R.id.legendContainer)

        // Clear existing items
        legendContainer.removeAllViews()

        val categoryData = mapOf(
            Category.HOUSING to 650.0f,
            Category.FOOD to 425.75f,
            Category.TRANSPORT to 232.50f,
            Category.ENTERTAINMENT to 205.02f
        )

        for ((category, amount) in categoryData) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER_VERTICAL
            }

            val colorView = View(this).apply {
                val params = LinearLayout.LayoutParams(12.dp, 12.dp)
                params.setMargins(0, 0, 6.dp, 0)
                layoutParams = params
                background = DrawableUtils.getCategoryCircle(this@DashboardActivity, category)
            }


            val label = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
                text = "${EmojiUtils.getCategoryEmoji(category)} ${
                    category.name.lowercase().replaceFirstChar { it.uppercase() }
                }"
                setTextColor(getColor(R.color.text_medium))
                textSize = 14f
            }

            val value = TextView(this).apply {
                text = "R${"%,.2f".format(amount)}"
                setTextColor(getColor(R.color.text_dark))
                textSize = 14f
            }

            row.addView(colorView)
            row.addView(label)
            row.addView(value)

            legendContainer.addView(row)
        }
    }

    /**
     * Sets up the recent transactions list.
     */
    private fun setupRecentTransactions() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recentTransactionsRecyclerView)

        val dummyTransactions = listOf(
            Transaction(1, "Checkers", "Food", 98.00, Date(), true),
            Transaction(2, "Uber", "Transport", 45.50, Date(), true),
            Transaction(3, "Salary", "Income", 2500.00, Date(), false)
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TransactionAdapter(dummyTransactions) { clickedTransaction ->
            TransactionDetailBottomSheet.newInstance(clickedTransaction)
                .show(supportFragmentManager, "TransactionDetail")
        }
    }


    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
