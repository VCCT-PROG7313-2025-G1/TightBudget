package com.example.tightbudget.utils

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.example.tightbudget.data.Category

/**
 * Utility class for emoji icons in the TightBudget app
 */
object EmojiUtils {

    // Category Emojis
    private const val EMOJI_HOUSING = "🏠"
    private const val EMOJI_FOOD = "🍔"
    private const val EMOJI_TRANSPORT = "🚗"
    private const val EMOJI_ENTERTAINMENT = "🎬"
    private const val EMOJI_SHOPPING = "🛍️"
    private const val EMOJI_UTILITIES = "💡"
    private const val EMOJI_HEALTH = "💊"
    private const val EMOJI_OTHER = "📋"

    // Achievement Emojis
    private const val EMOJI_SAVER = "💰"
    private const val EMOJI_CONSISTENT = "📅"
    private const val EMOJI_TRANSPORT_ACHIEVEMENT = "🚗"
    private const val EMOJI_LOCKED = "🔒"

    // Action Emojis
    private const val EMOJI_ADD_EXPENSE = "🧾"
    private const val EMOJI_VIEW_BUDGET = "📊"
    private const val EMOJI_GOALS = "🏆"
    private const val EMOJI_FLAME = "🔥"
    private const val EMOJI_PROFILE = "👤"

    // Transaction Type Emojis
    private const val EMOJI_SHOPPING_CART = "🛒"
    private const val EMOJI_GAS_STATION = "⛽"
    private const val EMOJI_FOOD_RESTAURANT = "🍗"
    private const val EMOJI_SALARY = "💼"
    private const val EMOJI_ADD = "➕"

    // Bottom Navigation Emojis
    private const val EMOJI_HOME = "🏠"
    private const val EMOJI_REPORTS = "📈"
    private const val EMOJI_WALLET = "👛"
    private const val EMOJI_SETTINGS = "⚙️"

    /**
     * Get emoji for a specific category
     */
    fun getCategoryEmoji(category: Category): String {
        return when (category) {
            Category.HOUSING -> EMOJI_HOUSING
            Category.FOOD -> EMOJI_FOOD
            Category.TRANSPORT -> EMOJI_TRANSPORT
            Category.ENTERTAINMENT -> EMOJI_ENTERTAINMENT
            Category.SHOPPING -> EMOJI_SHOPPING
            Category.UTILITIES -> EMOJI_UTILITIES
            Category.HEALTH -> EMOJI_HEALTH
            Category.OTHER -> EMOJI_OTHER
        }
    }

    /**
     * Get emoji for achievement badge
     */
    fun getAchievementEmoji(achievement: String): String {
        return when (achievement.lowercase()) {
            "saver", "budget master", "super saver" -> "💰"
            "streak keeper" -> "🔥"
            "transport", "transport pro" -> "🚗"
            "food manager" -> "🍽️"
            "consistent", "daily logger" -> "📅"
            "photographer" -> "📸"
            "housing pro" -> "🏠"
            "challenge master" -> "🏆"
            "fun manager" -> "🎥"
            "investor" -> "📈"
            "tech wizard" -> "💻"
            "budget guru" -> "👑"
            else -> "🔒" // locked or unknown or coming soon
        }
    }

    /**
     * Get emoji for action button
     */
    fun getActionEmoji(action: String): String {
        return when (action.lowercase()) {
            "add expense", "expense" -> EMOJI_ADD_EXPENSE
            "view budget", "budget" -> EMOJI_VIEW_BUDGET
            "goals" -> EMOJI_GOALS
            "streak", "flame" -> EMOJI_FLAME
            "profile" -> EMOJI_PROFILE
            "add" -> EMOJI_ADD
            else -> EMOJI_OTHER
        }
    }

    /**
     * Get emoji for transaction type
     */
    fun getTransactionEmoji(type: String): String {
        return when (type.lowercase()) {
            "groceries", "shopping", "checkers" -> EMOJI_SHOPPING_CART
            "transport", "gas", "fuel", "engen" -> EMOJI_GAS_STATION
            "restaurant", "food", "nando's", "nandos" -> EMOJI_FOOD_RESTAURANT
            "salary", "income", "deposit" -> EMOJI_SALARY
            else -> "📝" // Default note emoji
        }
    }

    /**
     * Get emoji for bottom navigation
     */
    fun getNavigationEmoji(tab: String): String {
        return when (tab.lowercase()) {
            "home", "dashboard" -> EMOJI_HOME
            "reports", "chart" -> EMOJI_REPORTS
            "wallet" -> EMOJI_WALLET
            "settings" -> EMOJI_SETTINGS
            else -> EMOJI_ADD
        }
    }

    /**
     * Set emoji with text in a TextView
     */
    fun setEmojiText(textView: TextView, emoji: String, text: String) {
        textView.text = "$emoji $text"
    }

    /**
     * Set emoji and text in a TextView with the emoji coloured
     */
    fun setColoredEmoji(textView: TextView, emoji: String, text: String, emojiColor: Int) {
        val fullText = "$emoji $text"
        val spannableString = SpannableString(fullText)
        spannableString.setSpan(
            ForegroundColorSpan(emojiColor),
            0,
            emoji.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
    }

    /**
     * Scale emoji text size properly for consistent display
     */
    fun getEmojiTextSize(context: Context, sizeDp: Int): Float {
        // Convert dp to pixels for consistent sizing
        return context.resources.displayMetrics.density * sizeDp
    }
}