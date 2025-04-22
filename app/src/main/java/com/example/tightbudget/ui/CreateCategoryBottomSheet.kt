package com.example.tightbudget.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tightbudget.databinding.FragmentCreateCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A bottom sheet dialog for creating a new custom category.
 */
class CreateCategoryBottomSheet : BottomSheetDialogFragment() {

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

            Log.d("CreateCategorySheet", "Save button clicked with name=$name, budget=$budget")

            if (name.isEmpty() || budget.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Category '$name' saved!", Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            }
        }

        Log.d("CreateCategorySheet", "Fragment loaded successfully")


        // Save category button
        binding.saveCategoryButton.setOnClickListener {
            val name = binding.categoryNameInput.text.toString().trim()
            val budget = binding.budgetInput.text.toString().trim()

            if (name.isEmpty() || budget.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // TODO: Add actual logic to save category
                Toast.makeText(requireContext(), "Category '$name' saved!", Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            }
        }

        // TODO: Populate iconGrid and colorGrid with selectable options
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}