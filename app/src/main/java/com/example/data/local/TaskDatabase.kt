package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class, BirthdayEntity::class], version = 3, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao
  abstract fun birthdayDao(): BirthdayDao

  companion object {
    @Volatile
    private var INSTANCE: TaskDatabase? = null

    fun getDatabase(context: Context): TaskDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TaskDatabase::class.java,
          "task_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
