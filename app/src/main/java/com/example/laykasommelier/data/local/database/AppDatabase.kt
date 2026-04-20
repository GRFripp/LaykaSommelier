package com.example.laykasommelier.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.laykasommelier.data.local.dao.*
import com.example.laykasommelier.data.local.entities.*

@Database(
    entities = [Drink::class, Descriptor::class, DescriptorCategory::class, DescriptorReview::class, Review::class, Source::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun drinkDao(): DrinkDao
    abstract fun reviewDao(): ReviewDao
    abstract  fun sourceDao(): SourceDao
    abstract fun descriptorCategoryDao(): DescriptorCategoryDao
    abstract fun descriptorReviewDao(): DescriptorReviewDao
    abstract fun descriptorDao(): DescriptorDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "layka_database"
                )
                    .fallbackToDestructiveMigration()  // ТОЛЬКО ДЛЯ РАЗРАБОТКИ
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}