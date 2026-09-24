package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.TaskItem
import com.example.model.TaskPriority

@Entity(
  tableName = "tasks",
  indices = [Index(value = ["userId"]), Index(value = ["userId", "date"])]
)
data class TaskEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val title: String,
  val description: String,
  val category: String,
  val priority: String, // HIGH, MEDIUM, LOW
  val date: String,
  val time: String?,
  val isCompleted: Boolean,
  val createdAt: Long,
  val updatedAt: Long
) {
  fun toModel(): TaskItem {
    val prio = try {
      TaskPriority.valueOf(priority)
    } catch (_: Exception) {
      TaskPriority.MEDIUM
    }
    return TaskItem(
      id = id,
      userId = userId,
      title = title,
      description = description,
      category = category,
      priority = prio,
      date = date,
      time = time,
      isCompleted = isCompleted,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
  }

  companion object {
    fun fromModel(model: TaskItem): TaskEntity {
      return TaskEntity(
        id = model.id,
        userId = model.userId,
        title = model.title,
        description = model.description,
        category = model.category,
        priority = model.priority.name,
        date = model.date,
        time = model.time,
        isCompleted = model.isCompleted,
        createdAt = model.createdAt,
        updatedAt = model.updatedAt
      )
    }
  }
}
