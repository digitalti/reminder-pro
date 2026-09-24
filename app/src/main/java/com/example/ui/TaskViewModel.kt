package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BirthdayRepository
import com.example.data.TaskRepository
import com.example.data.auth.AppUser
import com.example.data.auth.AuthManager
import com.example.data.local.TaskDatabase
import com.example.data.sync.FirestoreSyncManager
import com.example.model.BirthdayItem
import com.example.model.TaskItem
import com.example.model.TaskPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class AppSection {
  TASKS,
  BIRTHDAYS
}

enum class BirthdayAppTab {
  DASHBOARD,
  CONTACTS,
  WISHES,
  SETTINGS
}

enum class TaskTab {
  TODAY,
  UPCOMING,
  ALL,
  OVERDUE,
  COMPLETED
}

data class TaskStats(
  val totalToday: Int = 0,
  val completedToday: Int = 0,
  val progressPercent: Float = 0f,
  val totalPending: Int = 0,
  val highPriorityPending: Int = 0
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
  val authManager = AuthManager(application)
  private val database = TaskDatabase.getDatabase(application)
  private val syncManager = FirestoreSyncManager(application, database.taskDao())
  private val repository = TaskRepository(database.taskDao(), syncManager)
  private val birthdayRepository = BirthdayRepository(database.birthdayDao())

  val currentSection = MutableStateFlow(AppSection.TASKS)

  val currentUser: StateFlow<AppUser?> = authManager.currentUser
  val isSyncing: StateFlow<Boolean> = syncManager.isSyncing
  val isCloudSyncActive: StateFlow<Boolean> = syncManager.cloudSyncActive
  val isFirebaseConfigured: Boolean = authManager.isFirebaseConfigured

  val selectedDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
  val selectedTab = MutableStateFlow(TaskTab.TODAY)
  val selectedCategory = MutableStateFlow<String?>("All")
  val selectedPriority = MutableStateFlow<TaskPriority?>(null)
  val searchQuery = MutableStateFlow("")

  // Birthday search and filter
  val birthdaySearchQuery = MutableStateFlow("")
  val birthdayFilterCategory = MutableStateFlow("All")

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  private val _isAuthLoading = MutableStateFlow(false)
  val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

  private val _phoneOtpSent = MutableStateFlow(false)
  val phoneOtpSent: StateFlow<Boolean> = _phoneOtpSent.asStateFlow()

  private val _generatedOtp = MutableStateFlow<String?>(null)
  val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val allTasks: StateFlow<List<TaskItem>> = currentUser.flatMapLatest { user ->
    if (user != null) {
      syncManager.startSync(user.uid)
      repository.getTasksForUser(user.uid)
    } else {
      syncManager.stopSync()
      flowOf(emptyList())
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val allBirthdays: StateFlow<List<BirthdayItem>> = currentUser.flatMapLatest { user ->
    if (user != null) {
      birthdayRepository.getBirthdaysForUser(user.uid)
    } else {
      flowOf(emptyList())
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val todaysBirthdays: StateFlow<List<BirthdayItem>> = allBirthdays.map { list ->
    list.filter { it.isToday }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val next7DaysBirthdays: StateFlow<List<BirthdayItem>> = allBirthdays.map { list ->
    list.filter { it.isNext7Days }
      .sortedBy { it.daysUntilBirthday }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val overdueBirthdays: StateFlow<List<BirthdayItem>> = allBirthdays.map { list ->
    list.filter { it.isOverdue }
      .sortedBy { it.daysOverdue }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val upcomingBirthdays: StateFlow<List<BirthdayItem>> = next7DaysBirthdays

  val selectedAppTab = MutableStateFlow(BirthdayAppTab.DASHBOARD)

  val filteredBirthdays: StateFlow<List<BirthdayItem>> = combine(
    allBirthdays,
    birthdayFilterCategory,
    birthdaySearchQuery
  ) { list, filter, query ->
    val sorted = list.sortedBy { it.daysUntilBirthday }
    sorted.filter { item ->
      val matchesFilter = when (filter) {
        "All" -> true
        "Today 🎉" -> item.isToday
        "Upcoming" -> item.daysUntilBirthday in 1..14
        "Family" -> item.relationship.equals("Family", ignoreCase = true)
        "Friends" -> item.relationship.equals("Friend", ignoreCase = true)
        "Work" -> item.relationship.equals("Colleague", ignoreCase = true)
        else -> true
      }

      val matchesQuery = query.isBlank() ||
        item.name.contains(query, ignoreCase = true) ||
        item.notes.contains(query, ignoreCase = true) ||
        item.relationship.contains(query, ignoreCase = true)

      matchesFilter && matchesQuery
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null) {
          // Check if tasks should be seeded
          database.taskDao().getTasksForUser(user.uid).collect { list ->
            if (list.isEmpty()) {
              repository.seedInitialTasksIfEmpty(user.uid, list.size)
            }
          }
        }
      }
    }

    viewModelScope.launch {
      currentUser.collect { user ->
        if (user != null) {
          // Check if birthdays should be seeded
          database.birthdayDao().getBirthdaysForUser(user.uid).collect { list ->
            if (list.isEmpty()) {
              birthdayRepository.seedInitialBirthdaysIfEmpty(user.uid, list.size)
            }
          }
        }
      }
    }
  }

  data class FilterCriteria(
    val category: String?,
    val priority: TaskPriority?,
    val query: String
  )

  private val filterCriteria = combine(
    selectedCategory,
    selectedPriority,
    searchQuery
  ) { cat, prio, query ->
    FilterCriteria(cat, prio, query)
  }

  // Filtered tasks based on tab, date, category, priority, and search
  val visibleTasks: StateFlow<List<TaskItem>> = combine(
    allTasks,
    selectedTab,
    selectedDate,
    filterCriteria
  ) { tasks, tab, date, filter ->
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    tasks.filter { task ->
      // Tab filter
      val matchesTab = when (tab) {
        TaskTab.TODAY -> task.date == date
        TaskTab.UPCOMING -> task.date > today && !task.isCompleted
        TaskTab.ALL -> true
        TaskTab.OVERDUE -> task.date < today && !task.isCompleted
        TaskTab.COMPLETED -> task.isCompleted
      }

      // Category filter
      val matchesCategory = filter.category == null || filter.category == "All" || task.category.equals(filter.category, ignoreCase = true)

      // Priority filter
      val matchesPriority = filter.priority == null || task.priority == filter.priority

      // Search query filter
      val matchesSearch = filter.query.isBlank() ||
        task.title.contains(filter.query, ignoreCase = true) ||
        task.description.contains(filter.query, ignoreCase = true)

      matchesTab && matchesCategory && matchesPriority && matchesSearch
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Computed statistics
  val stats: StateFlow<TaskStats> = allTasks.combine(selectedDate) { tasks, date ->
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val todayTasks = tasks.filter { it.date == today }
    val totalToday = todayTasks.size
    val completedToday = todayTasks.count { it.isCompleted }
    val percent = if (totalToday > 0) completedToday.toFloat() / totalToday.toFloat() else 0f
    val totalPending = tasks.count { !it.isCompleted }
    val highPending = tasks.count { !it.isCompleted && it.priority == TaskPriority.HIGH }

    TaskStats(
      totalToday = totalToday,
      completedToday = completedToday,
      progressPercent = percent,
      totalPending = totalPending,
      highPriorityPending = highPending
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskStats())

  // Authentication operations
  fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.signInWithEmail(email, pass)
      _isAuthLoading.value = false
      result.fold(
        onSuccess = { onSuccess() },
        onFailure = { _authError.value = it.message ?: "Authentication failed." }
      )
    }
  }

  fun signUpWithEmail(email: String, pass: String, name: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.signUpWithEmail(email, pass, name)
      _isAuthLoading.value = false
      result.fold(
        onSuccess = { onSuccess() },
        onFailure = { _authError.value = it.message ?: "Registration failed." }
      )
    }
  }

  fun signInWithGoogle(context: Context, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.signInWithGoogle(context)
      _isAuthLoading.value = false
      result.fold(
        onSuccess = { onSuccess() },
        onFailure = { _authError.value = it.message ?: "Google Sign-In failed." }
      )
    }
  }

  fun signInAsGuest(onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.signInAsGuest()
      _isAuthLoading.value = false
      result.fold(
        onSuccess = { onSuccess() },
        onFailure = { _authError.value = it.message ?: "Guest access failed." }
      )
    }
  }

  fun sendPhoneOtp(phoneNumber: String) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.sendPhoneOtp(phoneNumber)
      _isAuthLoading.value = false
      result.fold(
        onSuccess = { otp ->
          _phoneOtpSent.value = true
          _generatedOtp.value = otp
        },
        onFailure = { _authError.value = it.message ?: "Failed to send OTP." }
      )
    }
  }

  fun verifyPhoneOtpAndSignIn(
    phoneNumber: String,
    code: String,
    name: String?,
    isSignUp: Boolean,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      _isAuthLoading.value = true
      _authError.value = null
      val result = authManager.verifyPhoneOtpAndSignIn(phoneNumber, code, name, isSignUp)
      _isAuthLoading.value = false
      result.fold(
        onSuccess = {
          _phoneOtpSent.value = false
          _generatedOtp.value = null
          onSuccess()
        },
        onFailure = { _authError.value = it.message ?: "OTP verification failed." }
      )
    }
  }

  fun resetPhoneOtpState() {
    _phoneOtpSent.value = false
    _generatedOtp.value = null
    _authError.value = null
  }

  fun clearAuthError() {
    _authError.value = null
  }

  fun signOut() {
    authManager.signOut()
    syncManager.stopSync()
  }

  // Task CRUD operations
  fun createTask(
    title: String,
    description: String,
    category: String,
    priority: TaskPriority,
    date: String,
    time: String?
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val newTask = TaskItem(
        id = UUID.randomUUID().toString(),
        userId = user.uid,
        title = title.trim(),
        description = description.trim(),
        category = category,
        priority = priority,
        date = date,
        time = time,
        isCompleted = false
      )
      repository.insertOrUpdate(newTask)
    }
  }

  fun updateTask(task: TaskItem) {
    viewModelScope.launch {
      repository.insertOrUpdate(task.copy(updatedAt = System.currentTimeMillis()))
    }
  }

  fun toggleTaskCompleted(task: TaskItem) {
    viewModelScope.launch {
      repository.toggleCompleted(task)
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      repository.deleteTask(task.id, task.userId)
    }
  }

  fun clearCompletedTasks() {
    val user = currentUser.value ?: return
    val completed = allTasks.value.filter { it.isCompleted }
    viewModelScope.launch {
      repository.clearCompleted(user.uid, completed)
    }
  }

  // Section navigation
  fun switchSection(section: AppSection) {
    currentSection.value = section
  }

  fun switchAppTab(tab: BirthdayAppTab) {
    selectedAppTab.value = tab
  }

  // Birthday CRUD operations
  fun createBirthday(
    name: String,
    birthMonth: Int,
    birthDay: Int,
    birthYear: Int?,
    relationship: String,
    phoneNumber: String?,
    notes: String,
    preferredLanguage: String = "English"
  ) {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val item = BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = user.uid,
        name = name.trim(),
        birthMonth = birthMonth,
        birthDay = birthDay,
        birthYear = birthYear,
        relationship = relationship,
        phoneNumber = phoneNumber?.trim()?.ifBlank { null },
        preferredLanguage = preferredLanguage,
        notes = notes.trim(),
        lastWishedYear = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
      )
      birthdayRepository.insertOrUpdate(item)
    }
  }

  fun updateBirthday(birthday: BirthdayItem) {
    viewModelScope.launch {
      birthdayRepository.insertOrUpdate(birthday.copy(updatedAt = System.currentTimeMillis()))
    }
  }

  fun deleteBirthday(birthday: BirthdayItem) {
    viewModelScope.launch {
      birthdayRepository.deleteBirthday(birthday.id, birthday.userId)
    }
  }

  fun markBirthdayWished(birthday: BirthdayItem) {
    val currentYear = LocalDate.now().year
    viewModelScope.launch {
      birthdayRepository.markAsWished(birthday.id, birthday.userId, currentYear)
    }
  }

  fun seedSampleData() {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      birthdayRepository.seedSampleBirthdays(user.uid)
    }
  }

  fun clearAllBirthdays() {
    val user = currentUser.value ?: return
    viewModelScope.launch {
      val items = allBirthdays.value
      items.forEach { birthdayRepository.deleteBirthday(it.id, user.uid) }
    }
  }
}
