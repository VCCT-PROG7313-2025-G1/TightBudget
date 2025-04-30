package com.example.tightbudget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.databinding.ActivityBudgetGoalsBinding
import com.example.tightbudget.databinding.ItemBudgetCategoryBinding
import com.example.tightbudget.models.BudgetGoal
import com.example.tightbudget.models.CategoryBudget
import com.example.tightbudget.ui.CategoryBudgetItem
import com.example.tightbudget.utils.EmojiUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * BudgetGoalsActivity handles the screen where users set their total monthly budget
 * and allocate amounts to different spending categories.
 */
class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetGoalsBinding
    private var currentBudget = 5000.0  // Default starting budget
    private var minimumSpendingGoal = 0.0 // Default minimum spending goal
    private val budgetIncrement = 500.0
    private val categoryItems = mutableListOf<CategoryBudgetItem>()
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // 1-12
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var existingBudgetGoalId = 0 // For updating existing goals
    private val TAG = "BudgetGoalsActivity"
    private var totalAllocated = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    /**
     * Retrieve the current user ID from shared preferences.
     * This is used to link the budget goal to the specific user.
     */
    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("current_user_id", -1)
    }

    /**
     * Load the current user's budget goal from the database.
     */
    private fun loadCurrentUserBudget() {
        val userId = getCurrentUserId()
        if (userId == -1) {
            Toast.makeText(this, "Please log in to set budget goals", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(this@BudgetGoalsActivity)
                val budgetGoalDao = db.budgetGoalDao()
                val categoryBudgetDao = db.categoryBudgetDao()
                val categoryDao = db.categoryDao()

                // Try to get budget for current month first
                var budgetGoal =
                    budgetGoalDao.getBudgetGoalForMonth(userId, currentMonth, currentYear)

                // If no budget for current month, get the most recent active budget
                if (budgetGoal == null) {
                    budgetGoal = budgetGoalDao.getActiveBudgetGoal(userId)
                }

                if (budgetGoal != null) {
                    // We found an existing budget
                    existingBudgetGoalId = budgetGoal.id
                    currentBudget = budgetGoal.totalBudget
                    minimumSpendingGoal = budgetGoal.minimumSpendingGoal

                    // Load category allocations
                    val categoryBudgets = categoryBudgetDao.getCategoryBudgetsForGoal(budgetGoal.id)

                    // Get all categories for their emoji and color
                    val allCategories = categoryDao.getAllCategories()

                    // Map category budgets to UI items
                    categoryItems.clear()
                    totalAllocated = 0.0

                    for (categoryBudget in categoryBudgets) {
                        // Find corresponding category for emoji and color
                        val category = allCategories.find { it.name == categoryBudget.categoryName }

                        val item = CategoryBudgetItem(
                            categoryName = categoryBudget.categoryName,
                            emoji = category?.emoji
                                ?: EmojiUtils.getCategoryEmoji(categoryBudget.categoryName),
                            color = category?.color ?: "#CCCCCC",
                            allocation = categoryBudget.allocation,
                            id = categoryBudget.id
                        )
                        categoryItems.add(item)
                        totalAllocated += categoryBudget.allocation
                    }

                    // Update UI
                    runOnUiThread {
                        updateDisplayedBudget()
                        updateCategoryList()
                        updateMonth()
                    }
                } else {
                    // No existing budget, load default categories
                    val allCategories = categoryDao.getAllCategories()

                    categoryItems.clear()
                    totalAllocated = 0.0

                    // Create default allocations for all categories
                    for (category in allCategories) {
                        val defaultAllocation = if (allCategories.size > 0)
                            currentBudget / allCategories.size else 0.0

                        val item = CategoryBudgetItem(
                            categoryName = category.name,
                            emoji = category.emoji,
                            color = category.color,
                            allocation = defaultAllocation
                        )
                        categoryItems.add(item)
                        totalAllocated += defaultAllocation
                    }

                    // Update UI
                    runOnUiThread {
                        updateDisplayedBudget()
                        updateCategoryList()
                        updateMonth()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading budget: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@BudgetGoalsActivity,
                        "Error loading budget: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Update the month display in the UI.
     */
    private fun updateMonth() {
        val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth - 1) // 0-based month in Calendar
        calendar.set(Calendar.YEAR, currentYear)

        binding.monthText.text = monthFormatter.format(calendar.time)
    }

    private fun updateCategoryList() {
        binding.categoryContainer.removeAllViews()

        for (item in categoryItems) {
            val categoryView = layoutInflater.inflate(
                R.layout.item_budget_category,
                binding.categoryContainer,
                false
            )

            // Find views
            val emoji = categoryView.findViewById<TextView>(R.id.categoryEmoji)
            val name = categoryView.findViewById<TextView>(R.id.categoryName)
            val average = categoryView.findViewById<TextView>(R.id.categoryAverage)
            val amountInput = categoryView.findViewById<EditText>(R.id.categoryAmountInput)
            val percentage = categoryView.findViewById<TextView>(R.id.categoryPercentage)

            // Set category info
            emoji.text = item.emoji
            name.text = item.categoryName

            // Set amount input
            amountInput.setText(String.format("%.2f", item.allocation))

            // Calculate and set percentage
            val percentValue =
                if (currentBudget > 0) (item.allocation / currentBudget) * 100 else 0.0
            percentage.text = "${percentValue.toInt()}%"

            // Set average spending (optional - you can calculate this from past transactions)
            // For now, we'll just show a placeholder
            average.text = "Avg: ${getCategoryAverage(item.categoryName)}"

            // Set up amount input change listener
            amountInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        val newValue = amountInput.text.toString().toDoubleOrNull() ?: 0.0
                        item.allocation = newValue

                        // Update percentage when amount changes
                        val newPercentage =
                            if (currentBudget > 0) (newValue / currentBudget) * 100 else 0.0
                        percentage.text = "${newPercentage.toInt()}%"

                        // Recalculate total
                        recalculateTotalAllocated()
                    } catch (e: Exception) {
                        // Handle parsing errors
                        Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT)
                            .show()
                        amountInput.setText(String.format("%.2f", item.allocation))
                    }
                }
            }

            binding.categoryContainer.addView(categoryView)
        }

        // Update total allocated
        binding.totalAllocated.text = "Total: R${"%,.2f".format(totalAllocated)}"
    }

    // Helper function to get average spending for a category (from transaction history)
    private fun getCategoryAverage(categoryName: String): String {
        // TODO: Implement logic to calculate average spending for the category
        // For now, return a placeholder value
        return "R0.00"
    }

    private fun recalculateTotalAllocated() {
        totalAllocated = categoryItems.sumOf { it.allocation }
        binding.totalAllocated.text = "Total: R${"%,.2f".format(totalAllocated)}"

        // Highlight if over budget
        binding.totalAllocated.setTextColor(
            getColor(
                if (totalAllocated > currentBudget) R.color.red_light else R.color.text_dark
            )
        )
    }


    /**
     * Setup all the event listeners and initial UI values.
     */
    private fun setupUI() {
        // Display the initial budget value
        updateDisplayedBudget()

        // Handle back button
        binding.backButton.setOnClickListener {
            finish()  // Closes the activity and returns to previous screen
        }

        // Increase total budget
        binding.increaseBudget.setOnClickListener {
            currentBudget += budgetIncrement
            updateDisplayedBudget()

            // Proportionally adjust category allocations
            if (totalAllocated > 0) {
                val proportion = currentBudget / (currentBudget - budgetIncrement)
                for (item in categoryItems) {
                    item.allocation *= proportion
                }
                updateCategoryList()
            }
        }

        // Decrease total budget
        binding.decreaseBudget.setOnClickListener {
            if (currentBudget > budgetIncrement) {
                val oldBudget = currentBudget
                currentBudget -= budgetIncrement
                updateDisplayedBudget()

                // Proportionally adjust category allocations
                if (totalAllocated > 0) {
                    val proportion = currentBudget / oldBudget
                    for (item in categoryItems) {
                        item.allocation *= proportion
                    }
                    updateCategoryList()
                }
            } else {
                Toast.makeText(this, "Budget cannot be less than R500", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle month change
        binding.changeDateButton.setOnClickListener {
            showMonthPicker()
        }

        // Add new category
        binding.addCategory.setOnClickListener {
            showCategoryPicker()
        }

        // Handle save changes
        binding.saveChangesButton.setOnClickListener {
            saveBudgetGoal()
        }

        // Handle copy previous
        binding.copyPreviousButton.setOnClickListener {
            copyPreviousMonth()
        }

        // Setup minimum spending goal
        binding.minimumGoalSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    minimumSpendingGoal = (progress / 100.0) * currentBudget
                    updateMinimumGoalDisplay()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    /**
     * Update the displayed minimum spending goal.
     */
    private fun updateMinimumGoalDisplay() {
        binding.minimumGoalValue.text = "R${"%,.2f".format(minimumSpendingGoal)}"

        // Calculate percentage of total budget
        val percentage = if (currentBudget > 0)
            (minimumSpendingGoal / currentBudget) * 100 else 0.0
        binding.minimumGoalPercentage.text = "${percentage.toInt()}% of budget"

        // Update seek bar
        binding.minimumGoalSeekBar.progress = percentage.toInt()
    }

    private fun showMonthPicker() {
        // This would show a date picker
        // For simplicity, just showing a toast
        Toast.makeText(this, "Month picker will be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun showCategoryPicker() {
        // This would show the existing category picker
        Toast.makeText(this, "Category picker will be implemented", Toast.LENGTH_SHORT).show()
    }

    /**
     * Copy the budget from the previous month.
     */
    private fun copyPreviousMonth() {
        val userId = getCurrentUserId()
        if (userId == -1) {
            Toast.makeText(this, "Please log in to use this feature", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(this@BudgetGoalsActivity)
                val budgetGoalDao = db.budgetGoalDao()

                // Get all budget goals
                val allGoals = budgetGoalDao.getAllBudgetGoalsForUser(userId)

                // Find a previous month (not current)
                val previousGoal = allGoals.firstOrNull {
                    it.year != currentYear || it.month != currentMonth
                }

                if (previousGoal != null) {
                    // Load the previous goal's category budgets
                    val categoryBudgetDao = db.categoryBudgetDao()
                    val previousCategoryBudgets =
                        categoryBudgetDao.getCategoryBudgetsForGoal(previousGoal.id)

                    // Update our current data
                    currentBudget = previousGoal.totalBudget
                    minimumSpendingGoal = previousGoal.minimumSpendingGoal

                    // Update category items
                    categoryItems.clear()
                    totalAllocated = 0.0

                    // Get all categories for emojis and colors
                    val categoryDao = db.categoryDao()
                    val allCategories = categoryDao.getAllCategories()

                    for (categoryBudget in previousCategoryBudgets) {
                        // Find corresponding category for emoji and color
                        val category = allCategories.find { it.name == categoryBudget.categoryName }

                        val item = CategoryBudgetItem(
                            categoryName = categoryBudget.categoryName,
                            emoji = category?.emoji
                                ?: EmojiUtils.getCategoryEmoji(categoryBudget.categoryName),
                            color = category?.color ?: "#CCCCCC",
                            allocation = categoryBudget.allocation
                        )
                        categoryItems.add(item)
                        totalAllocated += categoryBudget.allocation
                    }

                    // Update UI
                    runOnUiThread {
                        updateDisplayedBudget()
                        updateCategoryList()
                        updateMinimumGoalDisplay()
                        Toast.makeText(
                            this@BudgetGoalsActivity,
                            "Copied budget from ${previousGoal.month}/${previousGoal.year}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@BudgetGoalsActivity,
                            "No previous budget found to copy",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error copying previous budget: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@BudgetGoalsActivity,
                        "Error copying budget: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Save the budget goal to the database.
     */
    private fun saveBudgetGoal() {
        val userId = getCurrentUserId()
        if (userId == -1) {
            Toast.makeText(this, "Please log in to save budget goals", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(this@BudgetGoalsActivity)
                val budgetGoalDao = db.budgetGoalDao()
                val categoryBudgetDao = db.categoryBudgetDao()

                // First, deactivate all existing budget goals for this user
                budgetGoalDao.deactivateAllBudgetGoals(userId)

                // Create or update budget goal
                val budgetGoal = BudgetGoal(
                    id = if (existingBudgetGoalId > 0) existingBudgetGoalId else 0,
                    userId = userId,
                    month = currentMonth,
                    year = currentYear,
                    totalBudget = currentBudget,
                    minimumSpendingGoal = minimumSpendingGoal,
                    isActive = true
                )

                val budgetGoalId = if (existingBudgetGoalId > 0) {
                    budgetGoalDao.updateBudgetGoal(budgetGoal)
                    // Delete existing category budgets
                    categoryBudgetDao.deleteCategoryBudgetsForGoal(existingBudgetGoalId)
                    existingBudgetGoalId
                } else {
                    budgetGoalDao.insertBudgetGoal(budgetGoal).toInt()
                }

                // Save category budgets
                for (item in categoryItems) {
                    val categoryBudget = CategoryBudget(
                        id = 0, // Always insert new
                        budgetGoalId = budgetGoalId,
                        categoryName = item.categoryName,
                        allocation = item.allocation
                    )
                    categoryBudgetDao.insertCategoryBudget(categoryBudget)
                }

                runOnUiThread {
                    Toast.makeText(
                        this@BudgetGoalsActivity,
                        "Budget goals saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    existingBudgetGoalId = budgetGoalId // Update ID for future updates
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving budget goal: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@BudgetGoalsActivity,
                        "Error saving budget: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Update the displayed budget text across relevant views.
     */
    private fun updateDisplayedBudget() {
        val budgetText = "R${"%,.2f".format(currentBudget)}"
        binding.monthlyBudgetText.text = budgetText
        binding.currentBudgetDisplay.text = budgetText

        // Update minimum goal as well
        updateMinimumGoalDisplay()
    }
}