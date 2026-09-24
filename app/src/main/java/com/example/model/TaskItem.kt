package com.example.model

enum class TaskPriority {
  HIGH,
  MEDIUM,
  LOW
}

data class TaskItem(
  val id: String,
  val userId: String,
  val title: String,
  val description: String = "",
  val category: String = "Personal",
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val date: String, // Format: YYYY-MM-DD
  val time: String? = null, // Format: HH:mm
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
