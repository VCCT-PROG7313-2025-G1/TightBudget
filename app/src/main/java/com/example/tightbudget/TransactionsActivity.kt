package com.example.tightbudget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tightbudget.adapters.TransactionAdapter
import com.example.tightbudget.databinding.ActivityTransactionsBinding
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.ui.TransactionDetailBottomSheet
import java.util.*

/**
 * Activity that displays a scrollable list of all transactions (expenses and income).
 * It uses a RecyclerView with TransactionAdapter and supports filter controls.
 */
class TransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView and adapter
        setupRecyclerView()

        // Load sample data (to be replaced by real database or API)
        val transactions = generateMockTransactions()
        transactionAdapter.updateList(transactions)
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
