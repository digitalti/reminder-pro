package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskItem
import com.example.model.TaskPriority
import com.example.ui.AppSection
import com.example.ui.TaskTab
import com.example.ui.TaskViewModel
import com.example.ui.components.AddEditBirthdayDialog
import com.example.ui.components.AddTaskBottomSheet
import com.example.ui.components.TodayTasksPopupDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.screens.BirthdayWishingScreen
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40
import com.example.ui.theme.Indigo60
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.Sky40
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHomeScreen(
  viewModel: TaskViewModel
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val visibleTasks by viewModel.visibleTasks.collectAsState()
  val allTasks by viewModel.allTasks.collectAsState()
  val stats by viewModel.stats.collectAsState()
  val selectedTab by viewModel.selectedTab.collectAsState()
  val selectedDate by viewModel.selectedDate.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val isCloudSyncActive by viewModel.isCloudSyncActive.collectAsState()
  val currentSection by viewModel.currentSection.collectAsState()
  val todaysBirthdays by viewModel.todaysBirthdays.collectAsState()

  var showAddSheet by remember { mutableStateOf(false) }
  var taskToEdit by remember { mutableStateOf<TaskItem?>(null) }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showSearchField by remember { mutableStateOf(false) }
  var showTodayTasksPopup by remember { mutableStateOf(false) }
  var showAddBirthdayDialog by remember { mutableStateOf(false) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val categories = listOf("All", "Work", "Personal", "Health", "Shopping", "Learning", "Urgent")

  // Generate 7 days for the date selector strip
  val today = remember { LocalDate.now() }
  val dateList = remember {
    (-1..5).map { offset -> today.plusDays(offset.toLong()) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                if (currentSection == AppSection.TASKS) {
                  showTodayTasksPopup = true
                }
              }
              .padding(vertical = 4.dp, horizontal = 4.dp)
              .testTag("app_logo_title_btn")
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (currentSection == AppSection.TASKS) Indigo40 else Color(0xFFEC4899)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (currentSection == AppSection.TASKS) Icons.Default.CheckCircle else Icons.Default.Cake,
                contentDescription = "Section Icon",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (currentSection == AppSection.TASKS) "Daily Tasks" else "Birthday Wishes",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (currentSection == AppSection.TASKS)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                  else Color(0xFFFCE7F3)
                ) {
                  Text(
                    text = if (currentSection == AppSection.TASKS) "Today" else "🎉 Celebrations",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (currentSection == AppSection.TASKS)
                      MaterialTheme.colorScheme.onPrimaryContainer
                    else Color(0xFFBE185D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              // Sync Status Badge
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(
                      if (isCloudSyncActive) Emerald40 else Sky40,
                      CircleShape
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isCloudSyncActive) "Cloud Synced" else "Secure Local Mode",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isCloudSyncActive) Emerald40 else Sky40,
                  fontSize = 10.sp
                )
              }
            }
          }
        },
        actions = {
          if (currentSection == AppSection.TASKS) {
            IconButton(
              onClick = { showSearchField = !showSearchField },
              modifier = Modifier.testTag("action_search_btn")
            ) {
              Icon(Icons.Default.Search, contentDescription = "Search tasks")
            }
          }

          IconButton(
            onClick = {
              if (currentSection == AppSection.TASKS) {
                viewModel.switchSection(AppSection.BIRTHDAYS)
              } else {
                viewModel.switchSection(AppSection.TASKS)
              }
            },
            modifier = Modifier.testTag("action_toggle_section_btn")
          ) {
            BadgedBox(
              badge = {
                if (todaysBirthdays.isNotEmpty()) {
                  Badge(containerColor = Color(0xFFEC4899)) {
                    Text("${todaysBirthdays.size}")
                  }
                }
              }
            ) {
              Icon(
                imageVector = if (currentSection == AppSection.TASKS) Icons.Default.Cake else Icons.Default.CheckCircle,
                contentDescription = "Switch Section",
                tint = Indigo40
              )
            }
          }

          IconButton(
            onClick = { showProfileDialog = true },
            modifier = Modifier.testTag("action_profile_btn")
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = (currentUser?.displayName?.firstOrNull() ?: 'U').uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
      ) {
        NavigationBarItem(
          selected = currentSection == AppSection.TASKS,
          onClick = { viewModel.switchSection(AppSection.TASKS) },
          icon = {
            Icon(Icons.Default.CheckCircle, contentDescription = "Tasks")
          },
          label = { Text("Daily Tasks") },
          modifier = Modifier.testTag("nav_tasks_tab")
        )
        NavigationBarItem(
          selected = currentSection == AppSection.BIRTHDAYS,
          onClick = { viewModel.switchSection(AppSection.BIRTHDAYS) },
          icon = {
            BadgedBox(
              badge = {
                if (todaysBirthdays.isNotEmpty()) {
                  Badge(containerColor = Color(0xFFEC4899)) {
                    Text("${todaysBirthdays.size}")
                  }
                }
              }
            ) {
              Icon(Icons.Default.Cake, contentDescription = "Birthdays")
            }
          },
          label = { Text("Birthday Wishes") },
          modifier = Modifier.testTag("nav_birthdays_tab")
        )
      }
    },
    floatingActionButton = {
      if (currentSection == AppSection.TASKS) {
        ExtendedFloatingActionButton(
          onClick = {
            taskToEdit = null
            showAddSheet = true
          },
          icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
          text = { Text("New Task", fontWeight = FontWeight.Bold) },
          containerColor = Indigo40,
          contentColor = Color.White,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .navigationBarsPadding()
            .testTag("add_task_fab")
        )
      } else {
        ExtendedFloatingActionButton(
          onClick = {
            showAddBirthdayDialog = true
          },
          icon = { Icon(Icons.Default.Add, contentDescription = "Add Birthday") },
          text = { Text("New Birthday", fontWeight = FontWeight.Bold) },
          containerColor = Color(0xFF8B5CF6),
          contentColor = Color.White,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .navigationBarsPadding()
            .testTag("add_birthday_fab")
        )
      }
    }
  ) { innerPadding ->
    when (currentSection) {
      AppSection.TASKS -> {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          // Birthday Alert Banner for Today's celebrations
          if (todaysBirthdays.isNotEmpty()) {
            val firstBday = todaysBirthdays.first()
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFFDF2F8),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { viewModel.switchSection(AppSection.BIRTHDAYS) }
                .testTag("today_birthday_alert_banner")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(
                    Icons.Default.Cake,
                    contentDescription = null,
                    tint = Color(0xFFDB2777),
                    modifier = Modifier.size(20.dp)
                  )
                  Text(
                    text = "🎂 Today is ${firstBday.name}'s Birthday! Tap to send a wish 🎉",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9D174D),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                Icon(
                  Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = Color(0xFFDB2777),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          // Search Bar (if opened)
          AnimatedVisibility(visible = showSearchField) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { viewModel.searchQuery.value = it },
              placeholder = { Text("Search tasks...") },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
              trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                  IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_text_field")
            )
          }

          // Daily Progress Hero Card
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Today's Focus",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = if (stats.totalToday > 0)
                      "${stats.completedToday} of ${stats.totalToday} tasks finished"
                    else
                      "No tasks scheduled for today yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Text(
                  text = "${(stats.progressPercent * 100).toInt()}%",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Indigo60
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              val animatedProgress by animateFloatAsState(
                targetValue = stats.progressPercent,
                label = "progress"
              )
              LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = if (stats.progressPercent >= 1f) Emerald40 else Indigo40,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
              )

              if (stats.completedToday > 0 && selectedTab == TaskTab.TODAY) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                  horizontalArrangement = Arrangement.End
                ) {
                  TextButton(
                    onClick = { viewModel.clearCompletedTasks() },
                    modifier = Modifier.testTag("clear_completed_btn")
                  ) {
                    Text("Clear Completed", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                  }
                }
              }
            }
          }

          // Date Selector Strip (Horizontal Scroll)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            dateList.forEach { dateObj ->
              val dateStr = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)
              val isSelected = selectedDate == dateStr
              val isCurrentDay = dateObj == today
              val dayName = dateObj.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
              val dayNumber = dateObj.dayOfMonth.toString()

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Indigo40 else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    viewModel.selectedDate.value = dateStr
                    if (selectedTab != TaskTab.TODAY) {
                      viewModel.selectedTab.value = TaskTab.TODAY
                    }
                  }
                  .testTag("date_pill_$dateStr")
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = if (isCurrentDay) "Today" else dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = dayNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          // Task Filter Tabs (Today, Upcoming, All, Overdue, Completed)
          ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp,
            modifier = Modifier.height(44.dp)
          ) {
            TaskTab.values().forEach { tab ->
              Tab(
                selected = selectedTab == tab,
                onClick = { viewModel.selectedTab.value = tab },
                text = {
                  Text(
                    text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                  )
                },
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
              )
            }
          }

          // Category filter chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.forEach { cat ->
              val isSelected = selectedCategory == cat
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.selectedCategory.value = cat },
                label = { Text(cat, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("filter_cat_$cat")
              )
            }
          }

          // Task List
          if (visibleTasks.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                  modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "No tasks found",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Tap '+ New Task' below to create one.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .testTag("task_lazy_column"),
              contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(visibleTasks, key = { it.id }) { task ->
                TaskItemCard(
                  task = task,
                  onToggle = { viewModel.toggleTaskCompleted(task) },
                  onEdit = {
                    taskToEdit = task
                    showAddSheet = true
                  },
                  onDelete = { viewModel.deleteTask(task) }
                )
              }
            }
          }
        }
      }
      AppSection.BIRTHDAYS -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          BirthdayWishingScreen(
            viewModel = viewModel,
            onOpenAddBirthday = { showAddBirthdayDialog = true }
          )
        }
      }
    }
  }

  // Add / Edit Task Modal Bottom Sheet
  if (showAddSheet) {
    AddTaskBottomSheet(
      sheetState = sheetState,
      taskToEdit = taskToEdit,
      onDismiss = { showAddSheet = false },
      onSave = { title, desc, cat, prio, date, time ->
        if (taskToEdit != null) {
          viewModel.updateTask(
            taskToEdit!!.copy(
              title = title,
              description = desc,
              category = cat,
              priority = prio,
              date = date,
              time = time
            )
          )
        } else {
          viewModel.createTask(
            title = title,
            description = desc,
            category = cat,
            priority = prio,
            date = date,
            time = time
          )
        }
        showAddSheet = false
      }
    )
  }

  // Profile / Firebase Details Dialog
  if (showProfileDialog && currentUser != null) {
    UserProfileDialog(
      user = currentUser!!,
      isFirebaseConfigured = viewModel.isFirebaseConfigured,
      isCloudSyncActive = isCloudSyncActive,
      totalTasks = allTasks.size,
      completedTasks = allTasks.count { it.isCompleted },
      onDismiss = { showProfileDialog = false },
      onSignOut = {
        showProfileDialog = false
        viewModel.signOut()
      }
    )
  }

  // Today Tasks Popup Dialog (Triggered by App Logo)
  if (showTodayTasksPopup) {
    val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    val todayTasks = allTasks.filter { it.date == todayStr }

    TodayTasksPopupDialog(
      todayTasks = todayTasks,
      onDismiss = { showTodayTasksPopup = false },
      onToggleTask = { task -> viewModel.toggleTaskCompleted(task) },
      onAddNewTaskForToday = {
        showTodayTasksPopup = false
        taskToEdit = null
        viewModel.selectedDate.value = todayStr
        showAddSheet = true
      }
    )
  }

  // Add Birthday Dialog
  if (showAddBirthdayDialog) {
    AddEditBirthdayDialog(
      birthdayToEdit = null,
      onDismiss = { showAddBirthdayDialog = false },
      onSave = { name, month, day, year, relationship, phone, notes, language ->
        viewModel.createBirthday(name, month, day, year, relationship, phone, notes, language)
        showAddBirthdayDialog = false
      }
    )
  }
}

@Composable
fun TaskItemCard(
  task: TaskItem,
  onToggle: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val priorityColor = when (task.priority) {
    TaskPriority.HIGH -> PriorityHigh
    TaskPriority.MEDIUM -> PriorityMedium
    TaskPriority.LOW -> PriorityLow
  }

  val cardBg by animateColorAsState(
    targetValue = if (task.isCompleted)
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    else
      MaterialTheme.colorScheme.surface,
    label = "cardBg"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 1.5.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onToggle() }
      .testTag("task_item_${task.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Priority accent pill / left border
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(36.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(priorityColor)
      )

      Spacer(modifier = Modifier.width(12.dp))

      // Checkbox
      IconButton(
        onClick = onToggle,
        modifier = Modifier
          .size(36.dp)
          .testTag("check_task_${task.id}")
      ) {
        Icon(
          imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = if (task.isCompleted) "Mark pending" else "Mark completed",
          tint = if (task.isCompleted) Emerald40 else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Content Column
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
          color = if (task.isCompleted)
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          else
            MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        if (task.description.isNotBlank()) {
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tags & Meta Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Category chip
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(vertical = 2.dp)
          ) {
            Text(
              text = task.category,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          // Due Time / Date
          if (!task.time.isNullOrBlank()) {
            Text(
              text = "⏰ ${task.time}",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Action buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onEdit,
          modifier = Modifier
            .size(32.dp)
            .testTag("edit_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_task_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
