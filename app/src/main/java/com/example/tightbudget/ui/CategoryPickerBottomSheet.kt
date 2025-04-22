package com.example.tightbudget.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tightbudget.adapters.CategoryAdapter
import com.example.tightbudget.databinding.FragmentCategoryPickerBinding
import com.example.tightbudget.models.CategoryItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A bottom sheet to let the user choose a category or create a new one.
 */
class CategoryPickerBottomSheet(
    private val categoryList: List<CategoryItem>,
    private val onCategorySelected: (CategoryItem) -> Unit,
    private val onCreateNewClicked: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentCategoryPickerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoryAdapter(categoryList) { selectedCategory ->
            onCategorySelected(selectedCategory)
            dismiss()
        }

        binding.categoryRecyclerView.adapter = adapter

        // Close modal
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        // "Create New Category" clicked
        binding.createNewCategoryButton.setOnClickListener {
            onCreateNewClicked()
            dismiss()
        }
        // TODO: Add search filtering if desired
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}