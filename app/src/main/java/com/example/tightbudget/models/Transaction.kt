package com.example.tightbudget.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Represents a single transaction (income or expense) in the app.
 * This class is used to display items in the transaction list,
 * and is also passed to the transaction detail overlay when clicked.
 *
 * The @Parcelize annotation automatically generates the code
 * needed to send this object between Android components.
 */
@Parcelize
data class Transaction(
    val id: Int,              // Unique identifier for the transaction
    val merchant: String,     // Who you paid or were paid by
    val category: String,     // Category like 'Food', 'Salary', etc.
    val amount: Double,       // How much was spent or earned
    val date: Date,           // Date and time of the transaction
    val isExpense: Boolean    // True = expense, False = income
) : Parcelable  // Makes this data class sendable between screens
