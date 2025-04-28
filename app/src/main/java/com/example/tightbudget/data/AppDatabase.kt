package com.example.tightbudget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tightbudget.models.Category
import com.example.tightbudget.models.Transaction
import com.example.tightbudget.models.User

@Database(entities = [User::class, Category::class, Transaction::class], version = 3)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tightbudget_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}