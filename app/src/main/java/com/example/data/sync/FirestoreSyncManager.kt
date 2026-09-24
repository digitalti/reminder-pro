package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.model.TaskItem
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirestoreSyncManager(
  private val context: Context,
  private val taskDao: TaskDao
) {
  private val scope = CoroutineScope(Dispatchers.IO)

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _cloudSyncActive = MutableStateFlow(false)
  val cloudSyncActive: StateFlow<Boolean> = _cloudSyncActive.asStateFlow()

  private var snapshotListener: ListenerRegistration? = null
  private var currentUserId: String? = null

  private val firestore: FirebaseFirestore? by lazy {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        FirebaseFirestore.getInstance()
      } else {
        null
      }
    } catch (e: Throwable) {
      Log.w("FirestoreSyncManager", "Firestore not available: ${e.message}")
      null
    }
  }

  fun startSync(userId: String) {
    if (userId == currentUserId && snapshotListener != null) return
    stopSync()
    currentUserId = userId

    val db = firestore
    if (db == null) {
      _cloudSyncActive.value = false
      return
    }

    try {
      _cloudSyncActive.value = true
      _isSyncing.value = true

      snapshotListener = db.collection("users")
        .document(userId)
        .collection("tasks")
        .addSnapshotListener { snapshot, error ->
          _isSyncing.value = false
          if (error != null) {
            Log.w("FirestoreSyncManager", "Snapshot listen failed: ${error.message}")
            return@addSnapshotListener
          }

          if (snapshot != null && !snapshot.isEmpty) {
            scope.launch {
              val remoteEntities = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: return@mapNotNull null
                val desc = doc.getString("description") ?: ""
                val cat = doc.getString("category") ?: "Personal"
                val prio = doc.getString("priority") ?: "MEDIUM"
                val date = doc.getString("date") ?: ""
                val time = doc.getString("time")
                val completed = doc.getBoolean("isCompleted") ?: false
                val created = doc.getLong("createdAt") ?: System.currentTimeMillis()
                val updated = doc.getLong("updatedAt") ?: System.currentTimeMillis()

                TaskEntity(
                  id = id,
                  userId = userId,
                  title = title,
                  description = desc,
                  category = cat,
                  priority = prio,
                  date = date,
                  time = time,
                  isCompleted = completed,
                  createdAt = created,
                  updatedAt = updated
                )
              }
              taskDao.insertAll(remoteEntities)
            }
          }
        }
    } catch (e: Exception) {
      Log.w("FirestoreSyncManager", "Failed to start Firestore listener: ${e.message}")
      _cloudSyncActive.value = false
      _isSyncing.value = false
    }
  }

  fun stopSync() {
    snapshotListener?.remove()
    snapshotListener = null
    currentUserId = null
    _cloudSyncActive.value = false
    _isSyncing.value = false
  }

  fun pushTask(task: TaskItem) {
    val db = firestore ?: return
    scope.launch {
      try {
        val taskMap = hashMapOf(
          "id" to task.id,
          "userId" to task.userId,
          "title" to task.title,
          "description" to task.description,
          "category" to task.category,
          "priority" to task.priority.name,
          "date" to task.date,
          "time" to task.time,
          "isCompleted" to task.isCompleted,
          "createdAt" to task.createdAt,
          "updatedAt" to task.updatedAt
        )
        db.collection("users")
          .document(task.userId)
          .collection("tasks")
          .document(task.id)
          .set(taskMap)
      } catch (e: Exception) {
        Log.w("FirestoreSyncManager", "Failed to push task to cloud: ${e.message}")
      }
    }
  }

  fun deleteTask(taskId: String, userId: String) {
    val db = firestore ?: return
    scope.launch {
      try {
        db.collection("users")
          .document(userId)
          .collection("tasks")
          .document(taskId)
          .delete()
      } catch (e: Exception) {
        Log.w("FirestoreSyncManager", "Failed to delete task from cloud: ${e.message}")
      }
    }
  }
}
