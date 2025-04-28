package com.example.tightbudget.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tightbudget.adapters.CategoryAdapter
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.databinding.FragmentCategoryPickerBinding
import com.example.tightbudget.models.CategoryItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

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

        // Log the categories received in constructor
        Log.d("CategoryPicker", "Categories: $categoryList")

        // Use the categoryList passed in constructor instead of fetching again
        adapter = CategoryAdapter(categoryList) { selectedCategory ->
            onCategorySelected(selectedCategory)
            dismiss()
        }

        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecyclerView.adapter = adapter

        binding.closeButton.setOnClickListener { dismiss() }
        binding.createNewCategoryButton.setOnClickListener {
            onCreateNewClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}