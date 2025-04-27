package com.example.tightbudget

import android.app.Application
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.models.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        seedDefaultCategories()
    }

    private fun seedDefaultCategories() {
        val db = AppDatabase.getDatabase(this)
        val categoryDao = db.categoryDao()

        CoroutineScope(Dispatchers.IO).launch {
            val existingCategories = categoryDao.getAllCategories()
            if (existingCategories.isEmpty()) {
                val defaultCategories = listOf(
                    Category(name = "Food", emoji = "🍔", color = "#FF9800"),
                    Category(name = "Housing", emoji = "🏠", color = "#4CAF50"),
                    Category(name = "Transport", emoji = "🚗", color = "#2196F3"),
                    Category(name = "Entertainment", emoji = "🎮", color = "#9C27B0"),
                    Category(name = "Utilities", emoji = "💡", color = "#FFC107"),
                    Category(name = "Health", emoji = "💊", color = "#E91E63"),
                    Category(name = "Shopping", emoji = "🛍️", color = "#00BCD4"),
                    Category(name = "Education", emoji = "🎓", color = "#3F51B5")
                )
                categoryDao.insertAll(defaultCategories)
            }
        }
    }
}