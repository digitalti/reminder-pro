package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY isCompleted ASC, createdAt DESC")
  fun getTasksForUser(userId: String): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE userId = :userId AND date = :date ORDER BY isCompleted ASC, createdAt DESC")
  fun getTasksForUserAndDate(userId: String, date: String): Flow<List<TaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(task: TaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<TaskEntity>)

  @Query("DELETE FROM tasks WHERE id = :id AND userId = :userId")
  suspend fun deleteById(id: String, userId: String)

  @Query("DELETE FROM tasks WHERE userId = :userId AND isCompleted = 1")
  suspend fun clearCompleted(userId: String)

  @Query("SELECT * FROM tasks WHERE id = :id AND userId = :userId LIMIT 1")
  suspend fun getTaskById(id: String, userId: String): TaskEntity?
}
