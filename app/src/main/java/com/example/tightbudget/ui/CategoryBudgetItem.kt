package com.example.tightbudget.ui

data class CategoryBudgetItem(
    val categoryName: String,
    val emoji: String,      // Category emoji
    val color: String,      // Category color
    val allocation: Double, // Budget allocation
    val id: Int = 0         // Database ID
)