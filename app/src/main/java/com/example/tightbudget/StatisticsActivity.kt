package com.example.tightbudget

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tightbudget.databinding.ActivityStatisticsBinding

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding

    // List of period buttons to update styling dynamically
    private lateinit var periodButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupPeriodButtons()
        showStatsFor("Month") // Set default stats
    }

    /**
     * Highlight and respond to each period button (Week, Month, Quarter, Year)
     */
    private fun setupPeriodButtons() {
        periodButtons = listOf(
            binding.weekButton,
            binding.monthButton,
            binding.quarterButton,
            binding.yearButton
        )

        periodButtons.forEach { button ->
            button.setOnClickListener {
                highlightSelected(button)
                showStatsFor(button.text.toString())
            }
        }
    }

    /**
     * Visually highlight the selected period button
     */
    private fun highlightSelected(selectedButton: Button) {
        periodButtons.forEach { button ->
            val selected = button == selectedButton

            button.setBackgroundTintList(ContextCompat.getColorStateList(
                this, if (selected) R.color.teal_light else android.R.color.white))

            button.setTextColor(ContextCompat.getColor(
                this, if (selected) R.color.white else R.color.text_medium))

            button.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    /**
     * Shows placeholder stats for each period.
     * TODO: Connect this to real transaction data from Room.
     */
    private fun showStatsFor(period: String) {
        when (period) {
            "Week" -> {
                binding.totalSpentText.text = "R3 240,75"
                binding.budgetUsageText.text = "Budget: R5 000 – 65% used"
            }
            "Month" -> {
                binding.totalSpentText.text = "R15 320,45"
                binding.budgetUsageText.text = "Budget: R18 000 – 85% used"
            }
            "Quarter" -> {
                binding.totalSpentText.text = "R42 000,00"
                binding.budgetUsageText.text = "Budget: R55 000 – 76% used"
            }
            "Year" -> {
                binding.totalSpentText.text = "R168 000,00"
                binding.budgetUsageText.text = "Budget: R240 000 – 70% used"
            }
        }

        // TODO: Update chartPlaceholder views with actual chart data
    }

    /**
     * Allows navigation between screens using the bottom navigation bar
     */
    private fun setupBottomNavigation() {
        binding.bottomNavBar.selectedItemId = R.id.nav_reports

        binding.bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_reports -> true
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
}