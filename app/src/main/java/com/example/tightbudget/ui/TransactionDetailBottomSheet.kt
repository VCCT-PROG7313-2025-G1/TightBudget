package com.example.tightbudget.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.tightbudget.R
import com.example.tightbudget.databinding.FragmentTransactionDetailBinding
import com.example.tightbudget.models.Category
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.utils.DrawableUtils
import com.example.tightbudget.utils.EmojiUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

/**
 * A bottom sheet dialog that displays detailed information about a selected transaction.
 */
class TransactionDetailBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTransactionDetailBinding
    private lateinit var transaction: Transaction

    /**
     * Called when the bottom sheet dialog is created.
     * Sets a custom background for the bottom sheet.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            // Set fully transparent background for the bottom sheet
            bottomSheet?.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_background)

            // Remove any system-imposed rounded corners behind it
            (bottomSheet?.parent as? View)?.background =
                ContextCompat.getDrawable(requireContext(), android.R.color.transparent)
        }

        dialog.window?.attributes?.windowAnimations = R.style.DialogFadeAnimation
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the transaction object passed in via arguments
        arguments?.let {
            transaction = it.getParcelable("transaction")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply white circular background
        DrawableUtils.applyWhiteCircleBackground(binding.detailEmoji, requireContext())

        // Set the emoji based on category
        binding.detailEmoji.text = EmojiUtils.getCategoryEmoji(transaction.category)

        // Fill in details
        binding.detailMerchant.text = transaction.merchant
        binding.detailCategory.text = transaction.category
        binding.detailAmount.text = formatAmount(transaction.amount, transaction.isExpense)
        binding.detailDate.text = formatDate(transaction.date)
        binding.detailType.text = if (transaction.isExpense) "Expense" else "Income"

        // Close button
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.deleteButton.setOnClickListener {
            ConfirmDeleteDialogFragment {
                // TODO: Replace this with actual delete logic
                dismiss() // Close the bottom sheet after deletion
            }.show(parentFragmentManager, "ConfirmDeleteDialog")
        }
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun formatAmount(amount: Double, isExpense: Boolean): String {
        val sign = if (isExpense) "-" else "+"
        return "${sign}R${"%,.2f".format(amount)}"
    }

    companion object {
        fun newInstance(transaction: Transaction): TransactionDetailBottomSheet {
            val args = Bundle().apply {
                putParcelable("transaction", transaction)
            }
            return TransactionDetailBottomSheet().apply {
                arguments = args
            }
        }
    }
}
