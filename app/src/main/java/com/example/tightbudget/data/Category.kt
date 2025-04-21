package com.example.tightbudget.data

/**
 * Enum representing spending categories
 */
enum class Category {
    HOUSING,
    FOOD,
    TRANSPORT,
    ENTERTAINMENT,
    SHOPPING,
    UTILITIES,
    HEALTH,
    OTHER;

    companion object {
        fun fromString(value: String): Category {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                OTHER
            }
        }
    }
}