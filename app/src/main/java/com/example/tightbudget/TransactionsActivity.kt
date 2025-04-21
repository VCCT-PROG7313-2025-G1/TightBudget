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
            Transaction(1, "Checkers", "Food", 86.45, Date(), true),
            Transaction(2, "Engen Garage", "Transport", 342.50, Date(), true),
            Transaction(3, "Nando's", "Entertainment", 178.75, Date(), true),
            Transaction(4, "Salary Deposit", "Income", 12450.00, Date(), false)
        )
    }
}
