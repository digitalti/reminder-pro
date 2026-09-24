package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskItem
import com.example.model.TaskPriority
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskBottomSheet(
  sheetState: SheetState,
  taskToEdit: TaskItem? = null,
  onDismiss: () -> Unit,
  onSave: (title: String, desc: String, category: String, priority: TaskPriority, date: String, time: String?) -> Unit
) {
  val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
  val tomorrowStr = remember { LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) }

  var title by remember(taskToEdit) { mutableStateOf(taskToEdit?.title ?: "") }
  var description by remember(taskToEdit) { mutableStateOf(taskToEdit?.description ?: "") }
  var category by remember(taskToEdit) { mutableStateOf(taskToEdit?.category ?: "Personal") }
  var priority by remember(taskToEdit) { mutableStateOf(taskToEdit?.priority ?: TaskPriority.MEDIUM) }
  var selectedDate by remember(taskToEdit) { mutableStateOf(taskToEdit?.date ?: todayStr) }
  var selectedTime by remember(taskToEdit) { mutableStateOf<String?>(taskToEdit?.time ?: "09:00") }
  var titleError by remember { mutableStateOf(false) }

  val categories = listOf("Personal", "Work", "Health", "Shopping", "Learning", "Urgent")
  val quickTimes = listOf("09:00", "12:00", "15:00", "18:00", "20:00")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp)
        .navigationBarsPadding()
        .padding(bottom = 24.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (taskToEdit != null) "Edit Task" else "Add New Task",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("dismiss_task_sheet_btn")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close sheet")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title input
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (it.isNotBlank()) titleError = false
        },
        label = { Text("Task Title *") },
        placeholder = { Text("e.g., Complete project report") },
        isError = titleError,
        supportingText = {
          if (titleError) {
            Text("Title cannot be empty", color = MaterialTheme.colorScheme.error)
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Description input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description (Optional)") },
        placeholder = { Text("Add extra notes, checklist or links...") },
        maxLines = 3,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_desc_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Priority Selection
      Text(
        text = "Priority Level",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TaskPriority.values().forEach { prio ->
          val isSelected = priority == prio
          val color = when (prio) {
            TaskPriority.HIGH -> PriorityHigh
            TaskPriority.MEDIUM -> PriorityMedium
            TaskPriority.LOW -> PriorityLow
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { priority = prio }
              .padding(vertical = 10.dp)
          ) {
            Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(color, CircleShape)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = prio.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category Selection
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = category == cat
          FilterChip(
            selected = isSelected,
            onClick = { category = cat },
            label = { Text(cat) },
            leadingIcon = if (isSelected) {
              { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("category_chip_$cat")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Date Selection
      Text(
        text = "Due Date",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedDate == todayStr,
          onClick = { selectedDate = todayStr },
          label = { Text("Today") },
          leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
        FilterChip(
          selected = selectedDate == tomorrowStr,
          onClick = { selectedDate = tomorrowStr },
          label = { Text("Tomorrow") },
          leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Time Selection
      Text(
        text = "Due Time",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        quickTimes.forEach { qTime ->
          FilterChip(
            selected = selectedTime == qTime,
            onClick = { selectedTime = if (selectedTime == qTime) null else qTime },
            label = { Text(qTime, fontSize = 12.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save button
      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
            return@Button
          }
          onSave(
            title.trim(),
            description.trim(),
            category,
            priority,
            selectedDate,
            selectedTime
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_task_btn"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (taskToEdit != null) "Update Task" else "Create Task",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
