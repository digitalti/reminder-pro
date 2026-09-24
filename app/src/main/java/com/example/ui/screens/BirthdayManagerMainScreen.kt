package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BirthdayItem
import com.example.ui.AppSection
import com.example.ui.BirthdayAppTab
import com.example.ui.TaskViewModel
import com.example.ui.components.AddEditBirthdayDialog
import com.example.ui.components.SendBirthdayWishDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.screens.birthday.BirthdayContactsSection
import com.example.ui.screens.birthday.BirthdayDashboardSection
import com.example.ui.screens.birthday.BirthdaySettingsSection
import com.example.ui.screens.birthday.BirthdayWishStationSection
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayManagerMainScreen(
  viewModel: TaskViewModel
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val isCloudSyncActive by viewModel.isCloudSyncActive.collectAsState()
  val currentSection by viewModel.currentSection.collectAsState()
  val selectedAppTab by viewModel.selectedAppTab.collectAsState()
  val todaysBirthdays by viewModel.todaysBirthdays.collectAsState()
  val overdueBirthdays by viewModel.overdueBirthdays.collectAsState()

  var showAddBirthdayDialog by remember { mutableStateOf(false) }
  var birthdayToEdit by remember { mutableStateOf<BirthdayItem?>(null) }
  var prefilledContact by remember { mutableStateOf<Pair<String, String?>?>(null) }
  var birthdayForWish by remember { mutableStateOf<BirthdayItem?>(null) }
  var showProfileDialog by remember { mutableStateOf(false) }

  // If user explicitly switched to Tasks section, show TaskHomeScreen
  if (currentSection == AppSection.TASKS) {
    TaskHomeScreen(viewModel = viewModel)
    return
  }

  // Dialogs
  if (showAddBirthdayDialog || birthdayToEdit != null) {
    AddEditBirthdayDialog(
      birthdayToEdit = birthdayToEdit,
      initialName = prefilledContact?.first,
      initialPhone = prefilledContact?.second,
      onDismiss = {
        showAddBirthdayDialog = false
        birthdayToEdit = null
        prefilledContact = null
      },
      onSave = { name, month, day, year, relationship, phone, notes, language ->
        if (birthdayToEdit != null) {
          viewModel.updateBirthday(
            birthdayToEdit!!.copy(
              name = name,
              birthMonth = month,
              birthDay = day,
              birthYear = year,
              relationship = relationship,
              phoneNumber = phone,
              notes = notes,
              preferredLanguage = language
            )
          )
        } else {
          viewModel.createBirthday(
            name = name,
            birthMonth = month,
            birthDay = day,
            birthYear = year,
            relationship = relationship,
            phoneNumber = phone,
            notes = notes,
            preferredLanguage = language
          )
        }
        showAddBirthdayDialog = false
        birthdayToEdit = null
        prefilledContact = null
      }
    )
  }

  birthdayForWish?.let { bday ->
    SendBirthdayWishDialog(
      birthday = bday,
      onDismiss = { birthdayForWish = null },
      onMarkWished = {
        viewModel.markBirthdayWished(bday)
      }
    )
  }

  if (showProfileDialog && currentUser != null) {
    UserProfileDialog(
      user = currentUser!!,
      isFirebaseConfigured = viewModel.isFirebaseConfigured,
      isCloudSyncActive = isCloudSyncActive,
      totalTasks = 0,
      completedTasks = 0,
      onDismiss = { showProfileDialog = false },
      onSignOut = {
        showProfileDialog = false
        viewModel.signOut()
      }
    )
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
              .clickable { showProfileDialog = true }
              .padding(vertical = 4.dp, horizontal = 2.dp)
              .testTag("app_branding_btn")
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Cake,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Birthday Manager Pro",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFCE7F3)
                ) {
                  Text(
                    text = "PRO",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBE185D),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }

              // Status line
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isCloudSyncActive) Emerald40 else Color(0xFF0EA5E9))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (todaysBirthdays.isNotEmpty()) "🎉 ${todaysBirthdays.size} Celebrating Today!" else if (isCloudSyncActive) "Cloud Synced" else "Offline Ready",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = if (todaysBirthdays.isNotEmpty()) Color(0xFFBE185D) else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontWeight = if (todaysBirthdays.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        },
        actions = {
          // Switch to Tasks shortcut
          IconButton(
            onClick = { viewModel.switchSection(AppSection.TASKS) },
            modifier = Modifier.testTag("action_switch_to_tasks_btn")
          ) {
            Icon(Icons.Default.TaskAlt, contentDescription = "Daily Tasks", tint = Indigo40)
          }

          // User Profile button
          IconButton(
            onClick = { showProfileDialog = true },
            modifier = Modifier.testTag("action_profile_btn")
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Indigo40.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = Indigo40,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        // Tab 1: Dashboard
        NavigationBarItem(
          selected = selectedAppTab == BirthdayAppTab.DASHBOARD,
          onClick = { viewModel.switchAppTab(BirthdayAppTab.DASHBOARD) },
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
              Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
            }
          },
          label = { Text("Dashboard", fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_tab_dashboard")
        )

        // Tab 2: Contacts Directory
        NavigationBarItem(
          selected = selectedAppTab == BirthdayAppTab.CONTACTS,
          onClick = { viewModel.switchAppTab(BirthdayAppTab.CONTACTS) },
          icon = {
            BadgedBox(
              badge = {
                if (overdueBirthdays.isNotEmpty()) {
                  Badge(containerColor = Color(0xFFF59E0B)) {
                    Text("${overdueBirthdays.size}")
                  }
                }
              }
            ) {
              Icon(Icons.Default.People, contentDescription = "Contacts")
            }
          },
          label = { Text("Contacts", fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_tab_contacts")
        )

        // Tab 3: Wish Station
        NavigationBarItem(
          selected = selectedAppTab == BirthdayAppTab.WISHES,
          onClick = { viewModel.switchAppTab(BirthdayAppTab.WISHES) },
          icon = {
            Icon(Icons.Default.AutoAwesome, contentDescription = "Wish Station")
          },
          label = { Text("Wish Station", fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_tab_wishes")
        )

        // Tab 4: Settings
        NavigationBarItem(
          selected = selectedAppTab == BirthdayAppTab.SETTINGS,
          onClick = { viewModel.switchAppTab(BirthdayAppTab.SETTINGS) },
          icon = {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
          },
          label = { Text("Settings", fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_tab_settings")
        )
      }
    },
    floatingActionButton = {
      if (selectedAppTab == BirthdayAppTab.DASHBOARD || selectedAppTab == BirthdayAppTab.CONTACTS) {
        ExtendedFloatingActionButton(
          onClick = {
            birthdayToEdit = null
            showAddBirthdayDialog = true
          },
          icon = { Icon(Icons.Default.Add, contentDescription = null) },
          text = { Text("Add Birthday", fontWeight = FontWeight.Bold) },
          containerColor = Indigo40,
          contentColor = Color.White,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .navigationBarsPadding()
            .testTag("add_birthday_fab")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(
        targetState = selectedAppTab,
        label = "birthday_tabs_crossfade"
      ) { tab ->
        when (tab) {
          BirthdayAppTab.DASHBOARD -> {
            BirthdayDashboardSection(
              viewModel = viewModel,
              onOpenAddBirthday = {
                birthdayToEdit = null
                prefilledContact = null
                showAddBirthdayDialog = true
              },
              onEditBirthday = { bday ->
                birthdayToEdit = bday
              },
              onSendWish = { bday -> birthdayForWish = bday }
            )
          }

          BirthdayAppTab.CONTACTS -> {
            BirthdayContactsSection(
              viewModel = viewModel,
              onOpenAddBirthday = {
                birthdayToEdit = null
                prefilledContact = null
                showAddBirthdayDialog = true
              },
              onOpenAddBirthdayWithContact = { name, phone ->
                birthdayToEdit = null
                prefilledContact = Pair(name, phone)
                showAddBirthdayDialog = true
              },
              onEditBirthday = { bday -> birthdayToEdit = bday },
              onSendWish = { bday -> birthdayForWish = bday }
            )
          }

          BirthdayAppTab.WISHES -> {
            BirthdayWishStationSection(
              viewModel = viewModel,
              preselectedContact = null
            )
          }

          BirthdayAppTab.SETTINGS -> {
            BirthdaySettingsSection(
              viewModel = viewModel,
              onSwitchToTasks = {
                viewModel.switchSection(AppSection.TASKS)
              }
            )
          }
        }
      }
    }
  }
}
