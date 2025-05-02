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

    // Category maps for fuzzy matching
    private val categoryEmojiMap = mapOf(
        // Housing-related
        "housing" to "🏠",
        "home" to "🏠",
        "rent" to "🏠",
        "mortgage" to "🏠",
        "apartment" to "🏠",
        "utilities" to "💡",
        "electricity" to "💡",
        "water" to "💧",
        "gas" to "🔥",
        "internet" to "🌐",

        // Food-related
        "food" to "🍔",
        "groceries" to "🛒",
        "grocery" to "🛒",
        "restaurant" to "🍽️",
        "dining" to "🍽️",
        "takeout" to "🥡",
        "coffee" to "☕",

        // Transport-related
        "transport" to "🚗",
        "transportation" to "🚗",
        "travel" to "✈️",
        "gas" to "⛽",
        "fuel" to "⛽",
        "car" to "🚗",
        "petrol" to "⛽",
        "bus" to "🚌",
        "train" to "🚆",
        "uber" to "🚕",
        "taxi" to "🚕",

        // Entertainment-related
        "entertainment" to "🎮",
        "recreation" to "🎮",
        "movies" to "🎬",
        "games" to "🎮",
        "fun" to "🎉",
        "hobby" to "🎨",
        "music" to "🎵",
        "streaming" to "📺",
        "netflix" to "📺",

        // Shopping-related
        "shopping" to "🛍️",
        "clothes" to "👚",
        "clothing" to "👚",
        "shoes" to "👟",
        "accessories" to "👜",

        // Health-related
        "health" to "💊",
        "medical" to "🏥",
        "doctor" to "👨‍⚕️",
        "pharmacy" to "💊",
        "medicine" to "💊",
        "fitness" to "🏋️",
        "gym" to "🏋️",

        // Education-related
        "education" to "🎓",
        "school" to "🏫",
        "college" to "🎓",
        "university" to "🎓",
        "books" to "📚",
        "courses" to "📝",
        "tuition" to "🎓",

        // Income-related
        "income" to "💰",
        "salary" to "💰",
        "paycheck" to "💰",
        "earnings" to "💰",
        "bonus" to "💰",

        // Others
        "pets" to "🐶",
        "subscriptions" to "📱",
        "insurance" to "🛡️",
        "personal care" to "💅",
        "savings" to "💵",
        "childcare" to "🧸",
        "donations" to "🙏",
        "gifts" to "🎁",

        // Catch-all
        "other" to "📁",
        "miscellaneous" to "📁",
        "misc" to "📁"
    )

    /**
     * Get emoji for a specific category with improved fuzzy matching.
     * This method attempts to find the closest match for the category name.
     */
    fun getCategoryEmoji(category: String): String {
        // Normalise the category name
        val normalizedCategory = category.trim().lowercase()

        // Direct match first
        categoryEmojiMap[normalizedCategory]?.let { return it }

        // Try to find a partial match if no direct match
        for ((key, emoji) in categoryEmojiMap) {
            if (normalizedCategory.contains(key) || key.contains(normalizedCategory)) {
                return emoji
            }
        }

        // Return default if no match found
        return "📁" // Default fallback emoji
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