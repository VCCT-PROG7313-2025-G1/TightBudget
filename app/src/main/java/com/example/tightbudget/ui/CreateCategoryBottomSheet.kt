package com.example.tightbudget.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.databinding.FragmentCreateCategoryBinding
import com.example.tightbudget.models.Category
import com.example.tightbudget.utils.EmojiUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

/**
 * A bottom sheet dialog for creating a new custom category.
 */
class CreateCategoryBottomSheet : BottomSheetDialogFragment() {

    private var selectedEmoji: String = "📁"
    private var selectedColor: String = "#FF9800"

    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflate layout using ViewBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Handle UI setup after the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIconGrid()
        setupColorGrid()

        Log.d("CreateCategorySheet", "onViewCreated triggered")

        // Confirm binding is active
        Log.d("CreateCategorySheet", "Save button ref: ${binding.saveCategoryButton}")

        // Close button dismisses the bottom sheet
        binding.closeCreateButton.setOnClickListener {
            Log.d("CreateCategorySheet", "Close button clicked")
            dismiss()
        }


        // Save category button
        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameInput.text.toString().trim()
            val budget = binding.budgetInput.text.toString().trim()

            if (name.isEmpty() || budget.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (selectedEmoji.isEmpty()) {
                Toast.makeText(requireContext(), "Please pick an emoji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedColor.isEmpty()) {
                Toast.makeText(requireContext(), "Please pick a color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(requireContext())
            val categoryDao = db.categoryDao()

            val newCategory = Category(
                name = name,
                emoji = selectedEmoji,
                color = selectedColor
            )

            lifecycleScope.launch {
                categoryDao.insertCategory(newCategory)
                Toast.makeText(
                    requireContext(),
                    "Category '$name' created successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        Log.d("CreateCategorySheet", "Fragment loaded successfully")
    }

    private fun setupIconGrid() {
        val categoryNames = listOf(
            "Food", "Transport", "Entertainment", "Housing", "Utilities",
            "Health", "Shopping", "Education", "Travel", "Groceries",
            "Salary", "Gifts", "Pets", "Subscriptions", "Insurance",
            "Fitness", "Personal Care", "Savings", "Childcare", "Donations"
        )

        categoryNames.forEach { categoryName ->
            val emoji = EmojiUtils.getCategoryEmoji(categoryName)

            val emojiView = TextView(requireContext()).apply {
                text = emoji
                textSize = 24f
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    selectedEmoji = emoji
                    Toast.makeText(context, "Selected: $emoji", Toast.LENGTH_SHORT).show()
                }
            }

            binding.iconGrid.addView(emojiView)
        }
    }

    private fun setupColorGrid() {
        val colorOptions = listOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
            "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
            "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B"
        )

        colorOptions.forEach { colorHex ->
            val colorView = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(100, 100)
                setBackgroundColor(colorHex.toColorInt())
                setOnClickListener {
                    selectedColor = colorHex
                    Toast.makeText(context, "Selected color: $colorHex", Toast.LENGTH_SHORT).show()
                }
            }
            binding.colorGrid.addView(colorView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}