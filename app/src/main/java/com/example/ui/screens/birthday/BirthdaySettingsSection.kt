package com.example.ui.screens.birthday

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppSection
import com.example.ui.TaskViewModel
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40

@Composable
fun BirthdaySettingsSection(
  viewModel: TaskViewModel,
  onSwitchToTasks: () -> Unit
) {
  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()
  val isCloudSyncActive by viewModel.isCloudSyncActive.collectAsState()
  val allBirthdays by viewModel.allBirthdays.collectAsState()

  var showProfileDialog by remember { mutableStateOf(false) }
  var showClearConfirmDialog by remember { mutableStateOf(false) }

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

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      shape = RoundedCornerShape(20.dp),
      icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
      title = { Text("Clear All Birthdays?", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to delete all saved birthdays? This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearAllBirthdays()
            showClearConfirmDialog = false
            Toast.makeText(context, "All birthdays cleared", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("settings_lazy_column"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp, top = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Text(
        text = "Settings & Management",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Sync status, backup, sample data and preferences.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Account & Sync Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Indigo40),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
              }
              Column {
                Text(
                  text = currentUser?.displayName ?: currentUser?.email ?: "Guest User",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(if (isCloudSyncActive) Emerald40 else Color(0xFF0EA5E9))
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isCloudSyncActive) "Cloud Sync Enabled" else "Local Offline Database",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCloudSyncActive) Emerald40 else Color(0xFF0EA5E9)
                  )
                }
              }
            }

            OutlinedButton(
              onClick = { showProfileDialog = true },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("manage_profile_btn")
            ) {
              Text("Account", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Data Management Card: Sample Generator & Export
    item {
      Text(
        text = "Data Management",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Sample Data Seeder
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Indigo40.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo40, modifier = Modifier.size(20.dp))
              }
              Column {
                Text(
                  text = "Populate Sample Birthdays",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Adds contacts for Today, Next 7 Days & Overdue",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Button(
              onClick = {
                viewModel.seedSampleData()
                Toast.makeText(context, "Sample birthdays loaded! 🎂", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
              modifier = Modifier.testTag("seed_sample_btn")
            ) {
              Text("Load", fontSize = 12.sp)
            }
          }

          // Export Birthdays
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF0EA5E9).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(20.dp))
              }
              Column {
                Text(
                  text = "Export Contacts Summary",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Copy text summary of all birthdays",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            OutlinedButton(
              onClick = {
                val summary = buildString {
                  appendLine("🎂 Birthday Directory Export (${allBirthdays.size} Contacts)")
                  appendLine("==========================================")
                  allBirthdays.forEach {
                    appendLine("• ${it.name} | ${it.formattedBirthDate} | ${it.relationship} | ${it.preferredLanguage} | Phone: ${it.phoneNumber ?: "N/A"}")
                  }
                }
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Birthday Export", summary))
                Toast.makeText(context, "Directory exported to clipboard!", Toast.LENGTH_LONG).show()
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Export", fontSize = 12.sp)
            }
          }

          // Clear All Data
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
              }
              Column {
                Text(
                  text = "Clear All Birthdays",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.error
                )
                Text(
                  text = "Delete all saved birthdays locally",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            OutlinedButton(
              onClick = { showClearConfirmDialog = true },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
              modifier = Modifier.testTag("clear_all_btn")
            ) {
              Text("Clear", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Switch to Daily Tasks mode (Preserves prior feature!)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .clickable { onSwitchToTasks() }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Indigo40),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column {
              Text(
                text = "Switch to Daily Tasks Manager",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "View and manage your tasks & checklists",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Indigo40)
        }
      }
    }

    // App Info
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.Cake, contentDescription = null, tint = Indigo40, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Birthday Manager Pro",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Version 2.0 • Offline Room DB • Multi-Language Wishes",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
