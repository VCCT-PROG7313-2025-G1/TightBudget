package com.example.tightbudget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tightbudget.adapters.TransactionAdapter
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.databinding.ActivityTransactionsBinding
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.ui.TransactionDetailBottomSheet
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity that displays a scrollable list of all transactions (expenses and income).
 * It uses a RecyclerView with TransactionAdapter and supports filter controls.
 */
class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val TAG = "TransactionsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView and adapter
        setupRecyclerView()

        // Load transactions for current user
        loadUserTransactions()
    }

    /**
     * Prepares the RecyclerView with layout manager and adapter
     */
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            // When a transaction is clicked, this will be triggered
            val bottomSheet = TransactionDetailBottomSheet.newInstance(transaction)
            bottomSheet.show(supportFragmentManager, "TransactionDetail")
        }

        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TransactionsActivity)
            adapter = transactionAdapter
        }
    }

    /**
     * Loads transactions for the current logged-in user
     */
    private fun loadUserTransactions() {
        val userId = getCurrentUserId()

        // Update summary info
        binding.transactionsCount.text = "Loading transactions..."

        if (userId == -1) {
            // User not logged in, show mock data
            Log.d(TAG, "No user logged in, showing mock data")
            val mockTransactions = generateMockTransactions()
            transactionAdapter.updateList(mockTransactions)
            binding.transactionsCount.text = "${mockTransactions.size} transactions (sample)"
            return
        }

        // User is logged in, load their transactions
        val db = AppDatabase.getDatabase(this)
        val transactionDao = db.transactionDao()

        lifecycleScope.launch {
            try {
                val transactions = transactionDao.getAllTransactionsForUser(userId)

                runOnUiThread {
                    if (transactions.isEmpty()) {
                        // No transactions found
                        Log.d(TAG, "No transactions found for user $userId")
                        binding.transactionsCount.text = "No transactions found"
                        // You could show an empty state view here
                    } else {
                        // Update adapter with real data
                        Log.d(TAG, "Loaded ${transactions.size} transactions for user $userId")
                        transactionAdapter.updateList(transactions)
                        binding.transactionsCount.text = "${transactions.size} transactions"

                        // Update month summary if you have the data
                        // Calculate total spending for this month
                        val totalExpense = transactions
                            .filter { it.isExpense }
                            .sumOf { it.amount }

                        binding.monthSummary.text = "Total spent: R${"%,.2f".format(totalExpense)}"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading transactions", e)
                runOnUiThread {
                    Toast.makeText(
                        this@TransactionsActivity,
                        "Error loading transactions",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Fall back to mock data
                    val mockTransactions = generateMockTransactions()
                    transactionAdapter.updateList(mockTransactions)
                    binding.transactionsCount.text = "${mockTransactions.size} transactions (sample)"
                }
            }
        }
    }

    /**
     * Get current user ID from SharedPreferences
     */
    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("current_user_id", -1)
        Log.d(TAG, "Retrieved userId from SharedPreferences: $userId")
        return userId
    }

    /**
     * Generates sample transactions for testing UI
     */
    private fun generateMockTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                id = 1,
                userId = -1, // Dummy user ID for testing
                merchant = "Checkers",
                category = "Food",
                amount = 86.45,
                date = Date(),
                isExpense = true
            ),
            Transaction(
                id = 2,
                userId = -1,
                merchant = "Engen Garage",
                category = "Transport",
                amount = 342.50,
                date = Date(),
                isExpense = true
            ),
            Transaction(
                id = 3,
                userId = -1,
                merchant = "Nando's",
                category = "Entertainment",
                amount = 178.75,
                date = Date(),
                isExpense = true
            ),
            Transaction(
                id = 4,
                userId = -1,
                merchant = "Salary Deposit",
                category = "Income",
                amount = 12450.00,
                date = Date(),
                isExpense = false
            )
        )
    }
}