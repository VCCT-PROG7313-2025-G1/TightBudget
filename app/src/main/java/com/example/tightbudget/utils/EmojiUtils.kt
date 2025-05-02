package com.example.tightbudget.utils

import android.widget.TextView

/**
 * Utility class for emoji icons in the TightBudget app
 */
object EmojiUtils {

    // Category Emojis - Main reference for all category emojis
    private const val EMOJI_HOUSING = "🏠"
    private const val EMOJI_FOOD = "🍔"
    private const val EMOJI_TRANSPORT = "🚗"
    private const val EMOJI_ENTERTAINMENT = "🎬"
    private const val EMOJI_SHOPPING = "🛍️"
    private const val EMOJI_UTILITIES = "💡"
    private const val EMOJI_HEALTH = "💊"
    private const val EMOJI_OTHER = "📋"
    private const val EMOJI_INCOME = "💰"
    private const val EMOJI_UNKNOWN = "📁"

    // Standard text spacing between emoji and text
    private const val EMOJI_TEXT_SPACING = " "

    // Category maps for fuzzy matching
    private val categoryEmojiMap = mapOf(
        "housing" to EMOJI_HOUSING,
        "home" to EMOJI_HOUSING,
        "rent" to EMOJI_HOUSING,
        "mortgage" to EMOJI_HOUSING,
        "apartment" to EMOJI_HOUSING,
        "utilities" to EMOJI_UTILITIES,
        "electricity" to EMOJI_UTILITIES,
        "water" to "💧",
        "gas" to "🔥",
        "internet" to "🌐",
        "wifi" to "📶",

        // Food-related
        "food" to EMOJI_FOOD,
        "groceries" to "🛒",
        "grocery" to "🛒",
        "restaurant" to "🍽️",
        "dining" to "🍽️",
        "takeout" to "🥡",
        "coffee" to "☕",

        // Transport-related
        "transport" to EMOJI_TRANSPORT,
        "transportation" to EMOJI_TRANSPORT,
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
        "entertainment" to EMOJI_ENTERTAINMENT,
        "recreation" to "🎮",
        "movies" to EMOJI_ENTERTAINMENT,
        "games" to "🎮",
        "fun" to "🎉",
        "hobby" to "🎨",
        "music" to "🎵",
        "concert" to "🎤",
        "spotify" to "🎵",
        "streaming" to "📺",
        "netflix" to "📺",

        // Shopping-related
        "shopping" to EMOJI_SHOPPING,
        "clothes" to "👚",
        "clothing" to "👚",
        "shoes" to "👟",
        "accessories" to "👜",

        // Health-related
        "health" to EMOJI_HEALTH,
        "medical" to "🏥",
        "doctor" to "👨‍⚕️",
        "pharmacy" to EMOJI_HEALTH,
        "medicine" to EMOJI_HEALTH,
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
        "income" to EMOJI_INCOME,
        "salary" to EMOJI_INCOME,
        "paycheck" to EMOJI_INCOME,
        "earnings" to EMOJI_INCOME,
        "wages" to EMOJI_INCOME,
        "dividends" to EMOJI_INCOME,
        "interest" to EMOJI_INCOME,
        "interest income" to EMOJI_INCOME,
        "bonus" to EMOJI_INCOME,

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
        "other" to EMOJI_OTHER,
        "miscellaneous" to EMOJI_OTHER,
        "misc" to EMOJI_OTHER
    )

    /**
     * Get emoji for a specific category with improved fuzzy matching.
     * This is the CENTRAL method to get category emojis - all code should use this method
     * rather than maintaining separate emoji mappings.
     *
     * @param category The category name to find an emoji for
     * @return The emoji string for the category, with fallback to default emoji
     */
    fun getCategoryEmoji(category: String): String {
        // Handle null or empty case
        if (category.isNullOrBlank()) {
            return EMOJI_UNKNOWN
        }

        // Normalize the category name
        val normalizedCategory = category.trim().lowercase()

        // Direct match first
        categoryEmojiMap[normalizedCategory]?.let { return it }

        // Try specific category constants for exact matches
        when (normalizedCategory) {
            "food", "food & drink" -> return EMOJI_FOOD
            "transport", "transportation" -> return EMOJI_TRANSPORT
            "housing", "rent", "home" -> return EMOJI_HOUSING
            "entertainment" -> return EMOJI_ENTERTAINMENT
            "shopping" -> return EMOJI_SHOPPING
            "health", "healthcare" -> return EMOJI_HEALTH
            "utilities" -> return EMOJI_UTILITIES
            "income" -> return EMOJI_INCOME
        }

        // Try to find a partial match if no direct match
        for ((key, emoji) in categoryEmojiMap) {
            if (normalizedCategory.contains(key) || key.contains(normalizedCategory)) {
                return emoji
            }
        }

        // Return default if no match found
        return EMOJI_UNKNOWN
    }

    /**
     * Get a standard display text with emoji prefix
     * Ensures consistent spacing between emoji and text
     *
     * @param category The category name
     * @return Formatted string with emoji and text
     */
    fun getEmojiCategoryText(category: String): String {
        val emoji = getCategoryEmoji(category)
        return "$emoji$EMOJI_TEXT_SPACING$category"
    }

    /**
     * Set emoji with text in a TextView, with consistent spacing
     */
    fun setEmojiText(textView: TextView, emoji: String, text: String) {
        textView.text = "$emoji$EMOJI_TEXT_SPACING$text"
    }

    /**
     * Set emoji for a category in a TextView
     */
    fun setCategoryWithEmoji(textView: TextView, category: String) {
        setEmojiText(textView, getCategoryEmoji(category), category)
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
}