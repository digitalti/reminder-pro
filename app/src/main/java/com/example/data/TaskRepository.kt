package com.example.data

import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.data.sync.FirestoreSyncManager
import com.example.model.TaskItem
import com.example.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class TaskRepository(
  private val taskDao: TaskDao,
  private val syncManager: FirestoreSyncManager
) {

  fun getTasksForUser(userId: String): Flow<List<TaskItem>> {
    return taskDao.getTasksForUser(userId).map { entities ->
      entities.map { it.toModel() }
    }
  }

  fun getTasksForUserAndDate(userId: String, date: String): Flow<List<TaskItem>> {
    return taskDao.getTasksForUserAndDate(userId, date).map { entities ->
      entities.map { it.toModel() }
    }
  }

  suspend fun insertOrUpdate(task: TaskItem) {
    taskDao.insertOrUpdate(TaskEntity.fromModel(task))
    syncManager.pushTask(task)
  }

  suspend fun toggleCompleted(task: TaskItem) {
    val updated = task.copy(
      isCompleted = !task.isCompleted,
      updatedAt = System.currentTimeMillis()
    )
    insertOrUpdate(updated)
  }

  suspend fun deleteTask(taskId: String, userId: String) {
    taskDao.deleteById(taskId, userId)
    syncManager.deleteTask(taskId, userId)
  }

  suspend fun clearCompleted(userId: String, completedTasks: List<TaskItem>) {
    taskDao.clearCompleted(userId)
    completedTasks.forEach { task ->
      syncManager.deleteTask(task.id, userId)
    }
  }

  suspend fun seedInitialTasksIfEmpty(userId: String, existingCount: Int) {
    if (existingCount > 0) return
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

    val sampleTasks = listOf(
      TaskItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        title = "Welcome to Daily Tasks! 🎉",
        description = "Check out how your tasks sync securely with Firebase and local Room storage.",
        category = "Personal",
        priority = TaskPriority.HIGH,
        date = today,
        time = "09:00",
        isCompleted = false
      ),
      TaskItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        title = "Review project deliverables",
        description = "Prepare sprint review presentation and verify documentation.",
        category = "Work",
        priority = TaskPriority.HIGH,
        date = today,
        time = "11:30",
        isCompleted = false
      ),
      TaskItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        title = "30-minute cardio & stretch",
        description = "Maintain daily fitness goal with evening jog or yoga.",
        category = "Health",
        priority = TaskPriority.MEDIUM,
        date = today,
        time = "18:00",
        isCompleted = false
      ),
      TaskItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        title = "Weekly grocery shopping",
        description = "Buy fresh fruits, vegetables, almond milk, and pantry staples.",
        category = "Shopping",
        priority = TaskPriority.LOW,
        date = tomorrow,
        time = "10:00",
        isCompleted = false
      )
    )

    sampleTasks.forEach { insertOrUpdate(it) }
  }
}
