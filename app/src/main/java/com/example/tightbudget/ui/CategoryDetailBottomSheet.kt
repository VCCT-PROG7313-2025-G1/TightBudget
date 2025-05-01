package com.example.tightbudget.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tightbudget.R
import com.example.tightbudget.TransactionsActivity
import com.example.tightbudget.adapters.TransactionAdapter
import com.example.tightbudget.models.CategorySpendingItem
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.utils.ProgressBarUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

/**
 * BottomSheetDialogFragment that displays detailed information about a category's spending
 * and all transactions within that category.
 */
class CategoryDetailBottomSheet : BottomSheetDialogFragment() {

    private lateinit var category: CategorySpendingItem
    private lateinit var transactions: List<Transaction>
    private lateinit var startDate: Date
    private lateinit var endDate: Date

    // UI Components
    private lateinit var categoryEmojiText: TextView
    private lateinit var categoryNameText: TextView
    private lateinit var totalAmountText: TextView
    private lateinit var budgetAmountText: TextView
    private lateinit var remainingAmountText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var viewAllButton: Button
    private lateinit var closeButton: ImageView

    // RecyclerView adapter
    private lateinit var transactionAdapter: TransactionAdapter

    companion object {
        const val ARG_CATEGORY = "category"
        const val ARG_TRANSACTIONS = "transactions"
        const val ARG_START_DATE = "start_date"
        const val ARG_END_DATE = "end_date"

        fun newInstance(
            category: CategorySpendingItem,
            transactions: List<Transaction>,
            startDate: Date,
            endDate: Date
        ): CategoryDetailBottomSheet {
            val fragment = CategoryDetailBottomSheet()
            val args = Bundle()

            // Convert objects to serializable/parcelable format
            args.putString(ARG_CATEGORY, category.id)
            args.putParcelableArrayList(ARG_TRANSACTIONS, ArrayList(transactions))
            args.putLong(ARG_START_DATE, startDate.time)
            args.putLong(ARG_END_DATE, endDate.time)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_category_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views
        categoryEmojiText = view.findViewById(R.id.categoryEmojiLarge)
        categoryNameText = view.findViewById(R.id.categoryNameLarge)
        totalAmountText = view.findViewById(R.id.detailTotalAmount)
        budgetAmountText = view.findViewById(R.id.detailBudgetAmount)
        remainingAmountText = view.findViewById(R.id.detailRemainingAmount)
        progressBar = view.findViewById(R.id.detailProgressBar)
        transactionsRecyclerView = view.findViewById(R.id.detailTransactionsRecyclerView)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        viewAllButton = view.findViewById(R.id.viewAllTransactionsButton)
        closeButton = view.findViewById(R.id.closeButton)

        // Initialize RecyclerView
        transactionsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Set up close button
        closeButton.setOnClickListener {
            dismiss()
        }

        // Get data from arguments
        setupFromArguments()
    }

    private fun setupFromArguments() {
        // Get category and transactions from arguments
        arguments?.let { args ->
            val categoryId = args.getString(ARG_CATEGORY, "")
            val transactionList =
                args.getParcelableArrayList<Transaction>(ARG_TRANSACTIONS) ?: arrayListOf()
            val startTimestamp = args.getLong(ARG_START_DATE)
            val endTimestamp = args.getLong(ARG_END_DATE)

            // Recreate the category object and dates (will be done properly in real implementation)
            startDate = Date(startTimestamp)
            endDate = Date(endTimestamp)

            // Filter transactions just for this category
            transactions = transactionList.filter { it.category == categoryId }

            // Initialize with the data
            initializeWithCategory()
        }
    }

    private fun initializeWithCategory() {
        // Set category info
        categoryEmojiText.text = category.emoji
        categoryNameText.text = category.name

        // Set amounts
        totalAmountText.text = "R${String.format("%,.2f", category.amount)}"
        budgetAmountText.text = "R${String.format("%,.2f", category.budget)}"

        // Calculate remaining amount
        val remaining = category.budget - category.amount
        remainingAmountText.text = "R${String.format("%,.2f", remaining)}"

        // Set text color based on remaining amount
        if (remaining < 0) {
            remainingAmountText.setTextColor(requireContext().getColor(R.color.red_light))
        } else {
            remainingAmountText.setTextColor(requireContext().getColor(R.color.green_light))
        }

        // Set progress bar
        val progressPercentage = if (category.budget > 0) {
            (category.amount / category.budget) * 100
        } else {
            0.0
        }

        progressBar.max = 100
        progressBar.progress = progressPercentage.toInt().coerceIn(0, 100)

        // Apply color to progress bar based on spending vs budget
        ProgressBarUtils.applyBudgetStatusProgressBar(
            progressBar,
            requireContext(),
            category.amount.toFloat(),
            category.budget.toFloat()
        )

        // Set up transactions
        if (transactions.isEmpty()) {
            transactionsRecyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
        } else {
            transactionsRecyclerView.visibility = View.VISIBLE
            emptyStateText.visibility = View.GONE

            // Set up transaction adapter
            transactionAdapter = TransactionAdapter(transactions) { transaction ->
                // Show transaction detail when clicked
                val detailSheet = TransactionDetailBottomSheet.newInstance(transaction)
                detailSheet.show(parentFragmentManager, "TransactionDetail")
            }

            transactionsRecyclerView.adapter = transactionAdapter
        }

        // Set up view all button
        viewAllButton.setOnClickListener {
            val intent = Intent(requireContext(), TransactionsActivity::class.java).apply {
                putExtra("FILTER_CATEGORY", category.name)
                putExtra("START_DATE", startDate.time)
                putExtra("END_DATE", endDate.time)
            }
            startActivity(intent)
            dismiss()
        }
    }
}