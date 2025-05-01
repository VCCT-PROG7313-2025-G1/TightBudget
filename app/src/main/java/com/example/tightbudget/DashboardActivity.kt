package com.example.tightbudget

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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
import com.example.tightbudget.models.BudgetGoal
import com.example.tightbudget.models.CategoryBudget
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.ui.TransactionDetailBottomSheet
import com.example.tightbudget.utils.CategoryConstants
import com.example.tightbudget.utils.ChartUtils
import com.example.tightbudget.utils.DrawableUtils
import com.example.tightbudget.utils.EmojiUtils
import com.example.tightbudget.utils.ProgressBarUtils
import com.example.tightbudget.utils.DashboardDataManager
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar

/**
 * Dashboard screen showing financial summary, goals, charts and quick access buttons.
 */
class DashboardActivity : AppCompatActivity() {
    private val TAG = "DashboardActivity"

    // Database and data manager
    private lateinit var db: AppDatabase
    private lateinit var dataManager: DashboardDataManager

    // UI components for easy access
    private lateinit var welcomeTextView: TextView
    private lateinit var balanceAmountView: TextView
    private lateinit var totalBudgetView: TextView
    private lateinit var spentSoFarView: TextView
    private lateinit var remainingView: TextView
    private lateinit var chartContainer: FrameLayout
    private lateinit var legendContainer: LinearLayout

    private var currentUserId: Int = -1
    private var currentBudgetGoal: BudgetGoal? = null
    private var categoryBudgets: List<CategoryBudget> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize the database and data manager
        db = AppDatabase.getDatabase(this)
        dataManager = DashboardDataManager(db)

        // Find UI components
        welcomeTextView = findViewById(R.id.welcomeText)
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)
        balanceAmountView = root.findViewById(R.id.balanceAmount)
        totalBudgetView = root.findViewById(R.id.totalBudgetAmount)
        spentSoFarView = root.findViewById(R.id.spentSoFarAmount)
        remainingView = root.findViewById(R.id.remainingAmount)
        chartContainer = root.findViewById(R.id.chartContainer)
        legendContainer = root.findViewById(R.id.legendContainer)

        // Get current user ID
        currentUserId = getCurrentUserId()

        // Load user information and financial data
        loadUserInformation()
        loadFinancialData()

        // Open ProfileActivity when the user taps the profile icon
        findViewById<FrameLayout>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupBottomNavigation()
        setupQuickActions()
        setupNavigationButtons()
        setupAchievementPlaceholders()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to the dashboard
        loadFinancialData()
    }

    /**
     * Load all financial data for the dashboard
     */
    private fun loadFinancialData() {
        if (currentUserId == -1) {
            // Show placeholder data if no user is logged in
            showPlaceholderData()
            return
        }

        lifecycleScope.launch {
            try {
                // Load budget goal
                currentBudgetGoal = dataManager.loadActiveBudgetGoal(currentUserId)

                if (currentBudgetGoal != null) {
                    // Load category budgets
                    categoryBudgets = dataManager.loadCategoryBudgets(currentBudgetGoal!!.id)

                    // Load spending data
                    val spendingData = dataManager.getCurrentMonthSpendingByCategory(currentUserId)
                    val totalSpending = dataManager.getCurrentMonthTotalSpending(currentUserId)

                    // Update UI with real data
                    updateBudgetSummary(currentBudgetGoal!!, totalSpending)
                    updateCategoryProgressBars(spendingData)
                    updateSpendingChart(spendingData)
                } else {
                    // No budget goal found - show placeholder or prompt to create one
                    showNoBudgetGoalUI()
                }

                // Load transactions regardless of budget goal
                setupRecentTransactions()

            } catch (e: Exception) {
                Log.e(TAG, "Error loading financial data", e)
                Toast.makeText(
                    this@DashboardActivity,
                    "Error loading financial data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Load user information with proper fallback strategy
     */
    private fun loadUserInformation() {
        lifecycleScope.launch {
            try {
                if (currentUserId != -1) {
                    // User is logged in via ID from SharedPreferences
                    val user = db.userDao().getUserById(currentUserId)

                    if (user != null) {
                        // Set welcome message and balance
                        welcomeTextView.text = "Welcome back, ${user.fullName}!"
                        balanceAmountView.text = "R%.2f".format(user.balance)
                        Log.d(TAG, "Loaded user from SharedPreferences ID: ${user.fullName}")
                    } else {
                        // If user not found by ID, try the email from intent
                        val userEmail = intent.getStringExtra("USER_EMAIL")
                        if (!userEmail.isNullOrEmpty()) {
                            val userByEmail = db.userDao().getUserByEmail(userEmail)
                            if (userByEmail != null) {
                                welcomeTextView.text = "Welcome back, ${userByEmail.fullName}!"
                                balanceAmountView.text = "R%.2f".format(userByEmail.balance)
                                // Save the user ID since we found them by email
                                saveUserSession(userByEmail.id)
                                currentUserId = userByEmail.id
                                Log.d(TAG, "Loaded user from email and saved ID: ${userByEmail.id}")
                            } else {
                                showDefaultUserInfo()
                            }
                        } else {
                            showDefaultUserInfo()
                        }
                    }
                } else {
                    // If no user ID in preferences, try the email from intent
                    val userEmail = intent.getStringExtra("USER_EMAIL")
                    if (!userEmail.isNullOrEmpty()) {
                        val userByEmail = db.userDao().getUserByEmail(userEmail)
                        if (userByEmail != null) {
                            welcomeTextView.text = "Welcome back, ${userByEmail.fullName}!"
                            balanceAmountView.text = "R%.2f".format(userByEmail.balance)
                            // Save the user ID since we found them by email
                            saveUserSession(userByEmail.id)
                            currentUserId = userByEmail.id
                            Log.d(TAG, "Loaded user from email and saved ID: ${userByEmail.id}")
                        } else {
                            showDefaultUserInfo()
                        }
                    } else {
                        showDefaultUserInfo()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user information", e)
                showDefaultUserInfo()
            }
        }
    }

    private fun showDefaultUserInfo() {
        welcomeTextView.text = "Welcome, Guest!"
        balanceAmountView.text = "R0.00"
    }

    private fun updateBudgetSummary(budgetGoal: BudgetGoal, totalSpending: Double) {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Update budget summary numbers
        totalBudgetView.text = "R%.2f".format(budgetGoal.totalBudget)
        spentSoFarView.text = "R%.2f".format(totalSpending)

        val remaining = budgetGoal.totalBudget - totalSpending
        remainingView.text = "R%.2f".format(remaining)

        // Update the overall budget progress bar
        val overallProgressBar = root.findViewById<ProgressBar>(R.id.overallBudgetProgress)
        ProgressBarUtils.setProgress(overallProgressBar, totalSpending, budgetGoal.totalBudget)

        // Update minimum spending goal progress if available
        if (budgetGoal.minimumSpendingGoal > 0) {
            val minGoalText = root.findViewById<TextView>(R.id.minSpendingGoalText)
            val minGoalAmount = root.findViewById<TextView>(R.id.minSpendingGoalAmount)

            minGoalText.visibility = View.VISIBLE
            minGoalAmount.visibility = View.VISIBLE
            minGoalAmount.text = "R%.2f".format(budgetGoal.minimumSpendingGoal)

            // Add visual indicator if below minimum goal
            if (totalSpending < budgetGoal.minimumSpendingGoal) {
                minGoalAmount.setTextColor(getColor(R.color.orange))
            } else {
                minGoalAmount.setTextColor(getColor(R.color.text_medium))
            }
        }

        // Update the available balance and budget percentage
        calculateAndUpdateBalance(budgetGoal, totalSpending)
    }

    /**
     * Calculate available balance and budget percentage
     */
    private fun calculateAndUpdateBalance(budgetGoal: BudgetGoal, totalSpending: Double) {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Calculate available budget balance
        val availableBalance = budgetGoal.totalBudget - totalSpending
        balanceAmountView.text = "R%.2f".format(availableBalance)

        // Calculate percentage of budget used
        val percentUsed = if (budgetGoal.totalBudget > 0) {
            (totalSpending / budgetGoal.totalBudget) * 100
        } else {
            0.0
        }

        // Update percentage text
        val budgetPercentage = root.findViewById<TextView>(R.id.budgetPercentage)
        budgetPercentage?.text = "${percentUsed.toInt()} %"

        // Update progress bar
        val budgetProgressBar = root.findViewById<ProgressBar>(R.id.budgetProgressBar)
        budgetProgressBar?.progress = percentUsed.toInt().coerceIn(0, 100)

        // Set color based on percentage
        if (budgetProgressBar != null) {
            val progressColor = when {
                percentUsed > 90 -> getColor(R.color.red_light)
                percentUsed > 75 -> getColor(R.color.orange)
                else -> getColor(R.color.primary_purple_light)
            }
            budgetProgressBar.progressTintList = ColorStateList.valueOf(progressColor)
        }
    }

    /**
     * Updates category progress bars and dynamically creates UI for custom categories
     */
    private fun updateCategoryProgressBars(spendingData: Map<String, Double>) {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)
        val categoryBudgetMap = categoryBudgets.associateBy { it.categoryName }

        // Get the container where category items should be displayed
        val categoryContainer = root.findViewById<LinearLayout>(R.id.categoryContainer)

        if (categoryContainer == null) {
            Log.e(TAG, "Category container not found in layout")
            return
        }

        // Process fixed categories first (with predefined UI elements)
        updateFixedCategoryUI(root, spendingData, categoryBudgetMap)

        // Clear any previously created dynamic categories
        // Get count of fixed categories - assuming the first 4 items are fixed categories
        val fixedCategoryCount = Math.min(4, categoryContainer.childCount)

        // Remove only dynamic categories (keep the fixed ones)
        while (categoryContainer.childCount > fixedCategoryCount) {
            categoryContainer.removeViewAt(fixedCategoryCount)
        }

        // Now process any additional categories dynamically
        val processedCategories = setOf(
            CategoryConstants.HOUSING.lowercase(),
            CategoryConstants.FOOD.lowercase(),
            CategoryConstants.TRANSPORT.lowercase(),
            CategoryConstants.ENTERTAINMENT.lowercase()
        )

        // Debug logging
        Log.d(TAG, "Processing categories: spending=${spendingData.keys}, budgets=${categoryBudgetMap.keys}")

        // First, handle categories that have both spending and budget
        val combinedCategories = mutableSetOf<String>()

        // Loop through all spending categories
        for ((categoryName, amount) in spendingData) {
            // Skip already processed categories
            if (processedCategories.contains(categoryName.lowercase())) {
                continue
            }

            // Track this category as processed
            combinedCategories.add(categoryName.lowercase())

            // Find budget for this category
            val budget = categoryBudgetMap[categoryName]?.allocation ?:
            categoryBudgetMap.entries.firstOrNull {
                it.key.equals(categoryName, ignoreCase = true)
            }?.value?.allocation ?: 0.0

            // Create and add a dynamic category UI
            try {
                val categoryView = createCategoryView(categoryName, amount, budget)
                categoryContainer.addView(categoryView)
                Log.d(TAG, "Added dynamic category: $categoryName")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating view for category $categoryName: ${e.message}", e)
            }
        }

        // Then add categories that have budget but no spending yet
        for ((categoryName, budgetCategory) in categoryBudgetMap) {
            if (processedCategories.contains(categoryName.lowercase()) ||
                combinedCategories.contains(categoryName.lowercase()) ||
                spendingData.keys.any { it.equals(categoryName, ignoreCase = true) }
            ) {
                continue
            }

            // Create UI for budget categories with no spending
            try {
                val categoryView = createCategoryView(categoryName, 0.0, budgetCategory.allocation)
                categoryContainer.addView(categoryView)
                Log.d(TAG, "Added budget-only category: $categoryName")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating view for budget category $categoryName: ${e.message}", e)
            }
        }
    }

    /**
     * Updates the fixed UI elements for the standard categories
     */
    private fun updateFixedCategoryUI(
        root: View,
        spendingData: Map<String, Double>,
        categoryBudgetMap: Map<String, CategoryBudget>
    ) {
        // Get progress bars for the main categories
        val housingProgressBar = root.findViewById<ProgressBar>(R.id.housingProgressBar)
        val foodProgressBar = root.findViewById<ProgressBar>(R.id.foodProgressBar)
        val transportProgressBar = root.findViewById<ProgressBar>(R.id.transportProgressBar)
        val entertainmentProgressBar = root.findViewById<ProgressBar>(R.id.entertainmentProgressBar)

        // Normalise category names to handle capitalisation
        val normalizedSpendingData = spendingData.mapKeys { it.key.lowercase() }

        // Update Housing progress
        val housingSpending =
            findCategoryAmount(normalizedSpendingData, "housing", "home", "rent", "mortgage")
        val housingBudget = findCategoryBudget(categoryBudgetMap, "housing", "home", "rent")
        if (housingProgressBar != null) {
            ProgressBarUtils.setProgress(
                housingProgressBar,
                housingSpending,
                housingBudget.coerceAtLeast(0.01)
            )
            root.findViewById<TextView>(R.id.housingProgressText)?.text =
                "R%.2f/R%.2f".format(housingSpending, housingBudget)
            root.findViewById<TextView>(R.id.housingAmount)?.text =
                "R%.2f / R%.2f".format(housingSpending, housingBudget)
        }

        // Update Food progress
        val foodSpending =
            findCategoryAmount(normalizedSpendingData, "food", "groceries", "grocery")
        val foodBudget = findCategoryBudget(categoryBudgetMap, "food", "groceries", "grocery")
        if (foodProgressBar != null) {
            ProgressBarUtils.setProgress(
                foodProgressBar,
                foodSpending,
                foodBudget.coerceAtLeast(0.01)
            )
            root.findViewById<TextView>(R.id.foodProgressText)?.text =
                "R%.2f/R%.2f".format(foodSpending, foodBudget)
            root.findViewById<TextView>(R.id.foodAmount)?.text =
                "R%.2f / R%.2f".format(foodSpending, foodBudget)
        }

        // Update Transport progress
        val transportSpending = findCategoryAmount(
            normalizedSpendingData,
            "transport",
            "transportation",
            "travel",
            "fuel"
        )
        val transportBudget =
            findCategoryBudget(categoryBudgetMap, "transport", "transportation", "travel")
        if (transportProgressBar != null) {
            ProgressBarUtils.setProgress(
                transportProgressBar,
                transportSpending,
                transportBudget.coerceAtLeast(0.01)
            )
            root.findViewById<TextView>(R.id.transportProgressText)?.text =
                "R%.2f/R%.2f".format(transportSpending, transportBudget)
            root.findViewById<TextView>(R.id.transportAmount)?.text =
                "R%.2f / R%.2f".format(transportSpending, transportBudget)
        }

        // Update Entertainment progress
        val entertainmentSpending =
            findCategoryAmount(normalizedSpendingData, "entertainment", "recreation", "leisure")
        val entertainmentBudget =
            findCategoryBudget(categoryBudgetMap, "entertainment", "recreation")
        if (entertainmentProgressBar != null) {
            ProgressBarUtils.setProgress(
                entertainmentProgressBar,
                entertainmentSpending,
                entertainmentBudget.coerceAtLeast(0.01)
            )
            root.findViewById<TextView>(R.id.entertainmentProgressText)?.text =
                "R%.2f/R%.2f".format(entertainmentSpending, entertainmentBudget)
            root.findViewById<TextView>(R.id.entertainmentAmount)?.text =
                "R%.2f / R%.2f".format(entertainmentSpending, entertainmentBudget)
        }
    }

    /**
     *Helper function to find category amount by checking multiple possible category names
     */
    private fun findCategoryAmount(
        spendingData: Map<String, Double>,
        vararg categoryNames: String
    ): Double {
        for (name in categoryNames) {
            spendingData[name.lowercase()]?.let { return it }
        }
        return 0.0
    }

    /**
     * Helper function to find category budget by checking multiple possible category names
     */
    private fun findCategoryBudget(
        budgetMap: Map<String, CategoryBudget>,
        vararg categoryNames: String
    ): Double {
        for (name in categoryNames) {
            budgetMap[name]?.let { return it.allocation }
            // Try case-insensitive match if needed
            budgetMap.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.let {
                return it.value.allocation
            }
        }
        return 0.0
    }

    /**
     * Creates a simplified category view that matches the hardcoded design
     */
    private fun createCategoryView(categoryName: String, spending: Double, budget: Double): View {
        // Create a simple LinearLayout container
        val categoryView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 0, 0, 12.dp)
        }

        // Top row with category name and amount
        val topRow = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Category name with emoji
        val emoji = EmojiUtils.getCategoryEmoji(categoryName)
        val nameView = TextView(this).apply {
            text = "$emoji $categoryName"
            textSize = 14f
            setTextColor(getColor(R.color.text_medium))
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
            }
        }

        // Amount text (spent/budget)
        val amountView = TextView(this).apply {
            text = "R%.2f/R%.2f".format(spending, budget)
            textSize = 14f
            setTextColor(getColor(R.color.text_medium))
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
            }
        }

        // Progress bar
        val percentUsed = if (budget > 0) (spending / budget) * 100 else 0.0
        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                6.dp
            ).apply {
                topMargin = 4.dp
            }
            max = 100
            progress = percentUsed.toInt().coerceIn(0, 100)

            // Set color based on spending percentage
            val progressColor = when {
                percentUsed > 100 -> getColor(R.color.red_light)
                percentUsed > 85 -> getColor(R.color.orange)
                else -> getColor(R.color.teal_light)
            }
            progressTintList = ColorStateList.valueOf(progressColor)
            setBackgroundColor(getColor(R.color.background_gray))
        }

        // Assemble the view
        topRow.addView(nameView)
        topRow.addView(amountView)
        categoryView.addView(topRow)
        categoryView.addView(progressBar)

        return categoryView
    }

    private fun updateSpendingChart(spendingData: Map<String, Double>) {
        // Convert to the format needed by ChartUtils
        val chartData = spendingData.mapValues { it.value.toFloat() }

        // Create and display the chart
        val donutChart = ChartUtils.createDonutChartView(this, chartData)
        chartContainer.removeAllViews()
        chartContainer.addView(donutChart)

        // Update the spending legend
        updateSpendingLegend(spendingData)
    }

    private fun updateSpendingLegend(spendingData: Map<String, Double>) {
        // Clear existing items
        legendContainer.removeAllViews()

        if (spendingData.isEmpty()) {
            // Show a "No data" message if there's no spending
            val noDataText = TextView(this).apply {
                text = "No spending data for this period"
                textSize = 14f
                setTextColor(getColor(R.color.text_medium))
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 16.dp, 0, 16.dp)
            }
            legendContainer.addView(noDataText)
            return
        }

        // Create a row for each category
        for ((categoryName, amount) in spendingData) {
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
                background = DrawableUtils.getCategoryCircle(this@DashboardActivity, categoryName)
            }

            val label = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
                text = "${EmojiUtils.getCategoryEmoji(categoryName)} $categoryName"
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

    private fun showNoBudgetGoalUI() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Update budget summary with zeros
        totalBudgetView.text = "R0.00"
        spentSoFarView.text = "R0.00"
        remainingView.text = "R0.00"

        // Show a message prompting the user to create a budget
        val createBudgetMessage = root.findViewById<TextView>(R.id.createBudgetMessage)
        if (createBudgetMessage != null) {
            createBudgetMessage.visibility = View.VISIBLE
        }

        // Show empty chart
        chartContainer.removeAllViews()
        val emptyChart = ChartUtils.createDonutChartView(this, emptyMap())
        chartContainer.addView(emptyChart)

        // Clear legend
        legendContainer.removeAllViews()
        val noDataText = TextView(this).apply {
            text = "No budget set for this month. Create one to get started!"
            textSize = 14f
            setTextColor(getColor(R.color.text_medium))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 16.dp, 0, 16.dp)
        }
        legendContainer.addView(noDataText)

        // Reset progress bars
        val housingProgressBar = root.findViewById<ProgressBar>(R.id.housingProgressBar)
        val foodProgressBar = root.findViewById<ProgressBar>(R.id.foodProgressBar)
        val transportProgressBar = root.findViewById<ProgressBar>(R.id.transportProgressBar)
        val entertainmentProgressBar = root.findViewById<ProgressBar>(R.id.entertainmentProgressBar)

        ProgressBarUtils.setProgress(housingProgressBar, 0.0, 1.0)
        ProgressBarUtils.setProgress(foodProgressBar, 0.0, 1.0)
        ProgressBarUtils.setProgress(transportProgressBar, 0.0, 1.0)
        ProgressBarUtils.setProgress(entertainmentProgressBar, 0.0, 1.0)

        // Update the labels with zero amounts
        root.findViewById<TextView>(R.id.housingAmount).text = "R0.00 / R0.00"
        root.findViewById<TextView>(R.id.foodAmount).text = "R0.00 / R0.00"
        root.findViewById<TextView>(R.id.transportAmount).text = "R0.00 / R0.00"
        root.findViewById<TextView>(R.id.entertainmentAmount).text = "R0.00 / R0.00"
    }

    private fun showPlaceholderData() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Sample budget values
        val sampleBudget = 5000.0
        val sampleSpending = 1513.47

        // Show sample budget data
        totalBudgetView.text = "R%.2f".format(sampleBudget)
        spentSoFarView.text = "R%.2f".format(sampleSpending)
        remainingView.text = "R%.2f".format(sampleBudget - sampleSpending)

        // Update available balance and percentage
        val availableBalance = sampleBudget - sampleSpending
        balanceAmountView.text = "R%.2f".format(availableBalance)

        // Update budget percentage
        val percentUsed = (sampleSpending / sampleBudget) * 100
        val budgetPercentage = root.findViewById<TextView>(R.id.budgetPercentage)
        budgetPercentage?.text = "${percentUsed.toInt()} %"

        // Update budget progress bar
        val budgetProgressBar = root.findViewById<ProgressBar>(R.id.budgetProgressBar)
        budgetProgressBar?.progress = percentUsed.toInt()

        // Setup sample progress bars
        ProgressBarUtils.setProgress(root.findViewById(R.id.housingProgressBar), 650.0, 800.0)
        ProgressBarUtils.setProgress(root.findViewById(R.id.foodProgressBar), 425.75, 400.0)
        ProgressBarUtils.setProgress(root.findViewById(R.id.transportProgressBar), 232.50, 250.0)
        ProgressBarUtils.setProgress(
            root.findViewById(R.id.entertainmentProgressBar),
            205.02,
            150.0
        )

        // Update the labels with sample amounts
        root.findViewById<TextView>(R.id.housingAmount).text = "R650.00 / R800.00"
        root.findViewById<TextView>(R.id.foodAmount).text = "R425.75 / R400.00"
        root.findViewById<TextView>(R.id.transportAmount).text = "R232.50 / R250.00"
        root.findViewById<TextView>(R.id.entertainmentAmount).text = "R205.02 / R150.00"

        // Show sample chart
        val categoryData = mapOf(
            CategoryConstants.HOUSING to 650.0f,
            CategoryConstants.FOOD to 425.75f,
            CategoryConstants.TRANSPORT to 232.50f,
            CategoryConstants.ENTERTAINMENT to 205.02f
        )

        val donutChart = ChartUtils.createDonutChartView(this, categoryData)
        chartContainer.removeAllViews()
        chartContainer.addView(donutChart)

        // Populate sample spending legend
        populateSpendingLegend()
    }

    private fun saveUserSession(userId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putInt("current_user_id", userId).apply()
        Log.d(TAG, "Saved user session with ID: $userId")
    }

    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("current_user_id", -1)
        Log.d(TAG, "Retrieved userId from SharedPreferences: $userId")
        return userId
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

        root.findViewById<TextView>(R.id.viewAllSpendingButton)?.setOnClickListener {
            startActivity(Intent(this, CategorySpendingActivity::class.java))
        }

        // Hide badges button (Gamification feature not implemented yet)
        val allBadgesButton = root.findViewById<TextView>(R.id.allBadgesButton)
        allBadgesButton?.visibility = View.GONE

    }

    /**
     * Setup simple static placeholders for dashboard icons - no actual achievements yet
     */
    private fun setupAchievementPlaceholders() {
        val root = findViewById<View>(R.id.dashboardMainCardsRoot)

        // Simply hide the achievement section for Part 2 (Gamification feature not implemented yet)
        val achievementSection = root.findViewById<LinearLayout>(R.id.achievementsSection)
        achievementSection?.visibility = View.GONE
    }

    private fun populateSpendingLegend() {
        val legendContainer = findViewById<LinearLayout>(R.id.legendContainer)

        // Clear existing items
        legendContainer.removeAllViews()

        val categoryData = mapOf(
            CategoryConstants.HOUSING to 650.0,
            CategoryConstants.FOOD to 425.75,
            CategoryConstants.TRANSPORT to 232.50,
            CategoryConstants.ENTERTAINMENT to 205.02
        )

        for ((categoryName, amount) in categoryData) {
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
                background = DrawableUtils.getCategoryCircle(this@DashboardActivity, categoryName)
            }

            val label = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
                text = "${EmojiUtils.getCategoryEmoji(categoryName)} ${
                    categoryName.lowercase().replaceFirstChar { it.uppercase() }
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

        // Set up RecyclerView with empty adapter initially
        val transactionAdapter = TransactionAdapter(emptyList()) { clickedTransaction ->
            TransactionDetailBottomSheet.newInstance(clickedTransaction)
                .show(supportFragmentManager, "TransactionDetail")
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        // Check if user is logged in
        if (currentUserId == -1) {
            // Show placeholder data for not logged in users
            val dummyTransactions = listOf(
                Transaction(
                    id = 1,
                    userId = -1,
                    merchant = "Checkers",
                    category = "Food",
                    amount = 98.00,
                    date = Date(),
                    isExpense = true
                ),
                Transaction(
                    id = 2,
                    userId = -1,
                    merchant = "Uber",
                    category = "Transport",
                    amount = 45.50,
                    date = Date(),
                    isExpense = true
                ),
                Transaction(
                    id = 3,
                    userId = -1,
                    merchant = "Salary",
                    category = "Income",
                    amount = 2500.00,
                    date = Date(),
                    isExpense = false
                )
            )
            transactionAdapter.updateList(dummyTransactions)
            return
        }

        // Load actual transactions using the data manager
        lifecycleScope.launch {
            try {
                val transactions = dataManager.loadRecentTransactions(currentUserId)

                runOnUiThread {
                    if (transactions.isEmpty()) {
                        // Handle empty state - maybe show a message
                        val emptyView = root.findViewById<TextView>(R.id.emptyTransactionsMessage)
                        if (emptyView != null) {
                            emptyView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                        Log.d(TAG, "No transactions found for user $currentUserId")
                    } else {
                        // Update adapter with real data
                        val emptyView = root.findViewById<TextView>(R.id.emptyTransactionsMessage)
                        if (emptyView != null) {
                            emptyView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        Log.d(
                            TAG,
                            "Loaded ${transactions.size} transactions for user $currentUserId"
                        )
                        transactionAdapter.updateList(transactions)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading transactions", e)
                Toast.makeText(
                    this@DashboardActivity,
                    "Error loading transactions: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}