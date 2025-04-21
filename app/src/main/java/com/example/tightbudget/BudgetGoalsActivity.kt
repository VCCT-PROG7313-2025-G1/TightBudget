package com.example.tightbudget

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tightbudget.databinding.ActivityBudgetGoalsBinding

/**
 * BudgetGoalsActivity handles the screen where users set their total monthly budget
 * and allocate amounts to different spending categories.
 */
class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetGoalsBinding
    private var currentBudget = 18000.0  // This is just for placeholder logic
    private val budgetIncrement = 500.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
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
        }

        // Decrease total budget
        binding.decreaseBudget.setOnClickListener {
            if (currentBudget > budgetIncrement) {
                currentBudget -= budgetIncrement
                updateDisplayedBudget()
            } else {
                Toast.makeText(this, "Budget cannot be less than R500", Toast.LENGTH_SHORT).show()
            }
        }

        // Show placeholder message for changing the budget month
        binding.changeDateButton.setOnClickListener {
            Toast.makeText(this, "Change month feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // Placeholder action for adding a new category
        binding.addCategory.setOnClickListener {
            Toast.makeText(this, "Add Category feature coming soon", Toast.LENGTH_SHORT).show()
        }

        // Handle save changes
        binding.saveChangesButton.setOnClickListener {
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
        }

        // Handle copy previous
        binding.copyPreviousButton.setOnClickListener {
            Toast.makeText(this, "Copied previous month’s budget", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Update the displayed budget text across relevant views.
     */
    private fun updateDisplayedBudget() {
        val budgetText = "R${"%,.2f".format(currentBudget)}"
        binding.monthlyBudgetText.text = budgetText
        binding.currentBudgetDisplay.text = budgetText
        binding.totalAllocated.text = "Total: $budgetText"
    }
}