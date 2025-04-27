package com.example.tightbudget.utils

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.example.tightbudget.models.Category

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
    fun getCategoryEmoji(category: String): String {
        return when (category.lowercase()) {
            "food" -> "🍔"
            "transport" -> "🚗"
            "entertainment" -> "🎮"
            "housing" -> "🏠"
            "utilities" -> "💡"
            "health" -> "💊"
            "shopping" -> "🛍️"
            "education" -> "🎓"
            "travel" -> "✈️"
            "groceries" -> "🛒"
            "salary" -> "💰"
            "gifts" -> "🎁"
            "pets" -> "🐶"
            "subscriptions" -> "📺"
            "insurance" -> "🛡️"
            "fitness" -> "🏋️"
            "personal care" -> "💅"
            "savings" -> "💵"
            "childcare" -> "🧸"
            "donations" -> "🙏"
            "other" -> "📁"
            else -> "📁" // Default fallback emoji
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
            "add" -> "➕"
            "edit" -> "✏️"
            "delete" -> "🗑️"
            else -> "⚙️" // Default fallback emoji
        }
    }

    /**
     * Set emoji with text in a TextView
     */
    fun setEmojiText(textView: TextView, emoji: String, text: String) {
        textView.text = "$emoji $text"
    }

}