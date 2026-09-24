package com.example.ui.screens.birthday

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BirthdayItem
import com.example.ui.TaskViewModel
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40
import com.example.ui.theme.PriorityHigh
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BirthdayDashboardSection(
  viewModel: TaskViewModel,
  onOpenAddBirthday: () -> Unit,
  onEditBirthday: (BirthdayItem) -> Unit = {},
  onSendWish: (BirthdayItem) -> Unit
) {
  val context = LocalContext.current
  val allBirthdays by viewModel.allBirthdays.collectAsState()
  val todaysBirthdays by viewModel.todaysBirthdays.collectAsState()
  val next7DaysBirthdays by viewModel.next7DaysBirthdays.collectAsState()
  val overdueBirthdays by viewModel.overdueBirthdays.collectAsState()

  var selectedFilter by remember { mutableStateOf("All") }

  val todayFormatted = remember {
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("dashboard_lazy_column"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header Info Banner
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Birthday Dashboard",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = todayFormatted,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Button(
            onClick = onOpenAddBirthday,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("header_add_contact_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Birthday", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 4 Key Metric Cards Row
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Today's Birthdays
          MetricCard(
            title = "Today's",
            count = todaysBirthdays.size,
            subtitle = if (todaysBirthdays.isNotEmpty()) "🎉 Celebrate!" else "None today",
            gradient = listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
            icon = Icons.Default.Celebration,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_card_today")
              .clickable { selectedFilter = "Today" }
          )

          // Next 7 Days
          MetricCard(
            title = "Next 7 Days",
            count = next7DaysBirthdays.size,
            subtitle = "Upcoming",
            gradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
            icon = Icons.Default.DateRange,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_card_next7")
              .clickable { selectedFilter = "Next 7 Days" }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Overdue / Missed
          MetricCard(
            title = "Overdue",
            count = overdueBirthdays.size,
            subtitle = if (overdueBirthdays.isNotEmpty()) "⚠️ Missed wishes" else "All caught up",
            gradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
            icon = Icons.Default.History,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_card_overdue")
              .clickable { selectedFilter = "Overdue" }
          )

          // Total Contacts
          MetricCard(
            title = "Total Contacts",
            count = allBirthdays.size,
            subtitle = "In Directory",
            gradient = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
            icon = Icons.Default.People,
            modifier = Modifier
              .weight(1f)
              .testTag("metric_card_total")
              .clickable { selectedFilter = "All" }
          )
        }
      }
    }

    // Filter Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val filters = listOf(
          "All" to allBirthdays.size,
          "Today 🎉" to todaysBirthdays.size,
          "Next 7 Days" to next7DaysBirthdays.size,
          "Overdue ⚠️" to overdueBirthdays.size
        )
        filters.forEach { (label, count) ->
          val isSelected = selectedFilter.startsWith(label.take(4)) || (label == "All" && selectedFilter == "All")
          FilterChip(
            selected = isSelected,
            onClick = { selectedFilter = label.takeWhile { it != ' ' } },
            label = { Text("$label ($count)", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Indigo40.copy(alpha = 0.15f),
              selectedLabelColor = Indigo40
            ),
            modifier = Modifier.testTag("filter_chip_${label.take(3)}")
          )
        }
      }
    }

    // SECTION 1: TODAY'S BIRTHDAYS (Hero highlight)
    if (selectedFilter == "All" || selectedFilter.startsWith("Today")) {
      item {
        SectionHeader(
          title = "Today's Celebrations 🎉",
          count = todaysBirthdays.size,
          color = Color(0xFFEC4899)
        )
      }

      if (todaysBirthdays.isEmpty()) {
        item {
          EmptySectionCard(
            message = "No birthdays today! Enjoy a peaceful day, or check the Next 7 Days section.",
            icon = Icons.Default.Cake
          )
        }
      } else {
        items(todaysBirthdays, key = { "today_${it.id}" }) { bday ->
          TodayCelebrationCard(
            item = bday,
            onEdit = { onEditBirthday(bday) },
            onSendWish = { onSendWish(bday) },
            onCall = { bday.phoneNumber?.let { phone -> makeCall(context, phone) } },
            onSms = { bday.phoneNumber?.let { phone -> sendSms(context, phone, "Happy Birthday, ${bday.name}! 🎂🎉") } },
            onWhatsApp = { bday.phoneNumber?.let { phone -> openWhatsApp(context, phone, "Happy Birthday, ${bday.name}! 🎂🎉") } },
            onMarkWished = { viewModel.markBirthdayWished(bday) }
          )
        }
      }
    }

    // SECTION 2: NEXT 7 DAYS (UPCOMING)
    if (selectedFilter == "All" || selectedFilter.startsWith("Next")) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
          title = "Next 7 Days (Upcoming)",
          count = next7DaysBirthdays.size,
          color = Color(0xFF6366F1)
        )
      }

      if (next7DaysBirthdays.isEmpty()) {
        item {
          EmptySectionCard(
            message = "No birthdays scheduled in the next 7 days.",
            icon = Icons.Default.DateRange
          )
        }
      } else {
        items(next7DaysBirthdays, key = { "next7_${it.id}" }) { bday ->
          UpcomingBirthdayCard(
            item = bday,
            onEdit = { onEditBirthday(bday) },
            onSendWish = { onSendWish(bday) }
          )
        }
      }
    }

    // SECTION 3: OVERDUE / MISSED BIRTHDAYS
    if (selectedFilter == "All" || selectedFilter.startsWith("Overdue")) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(
          title = "Overdue Birthdays (Missed Wishes) ⚠️",
          count = overdueBirthdays.size,
          color = Color(0xFFD97706)
        )
      }

      if (overdueBirthdays.isEmpty()) {
        item {
          EmptySectionCard(
            message = "All caught up! No overdue or missed birthdays.",
            icon = Icons.Default.CheckCircle
          )
        }
      } else {
        items(overdueBirthdays, key = { "overdue_${it.id}" }) { bday ->
          OverdueBirthdayCard(
            item = bday,
            onEdit = { onEditBirthday(bday) },
            onSendBelatedWish = { onSendWish(bday) },
            onMarkWished = { viewModel.markBirthdayWished(bday) }
          )
        }
      }
    }

    // If total directory is empty, show sample seeder CTA
    if (allBirthdays.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo40, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Get Started with Sample Birthdays",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Add pre-loaded contacts featuring Today, Next 7 Days, and Overdue states to explore all features instantly!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = { viewModel.seedSampleData() },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Indigo40)
            ) {
              Text("Populate Sample Birthdays")
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  count: Int,
  subtitle: String,
  gradient: List<Color>,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.horizontalGradient(gradient))
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.9f)
          )
        }

        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    Surface(
      shape = RoundedCornerShape(8.dp),
      color = color.copy(alpha = 0.15f)
    ) {
      Text(
        text = "$count",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
      )
    }
  }
}

@Composable
private fun TodayCelebrationCard(
  item: BirthdayItem,
  onEdit: () -> Unit = {},
  onSendWish: () -> Unit,
  onCall: () -> Unit,
  onSms: () -> Unit,
  onWhatsApp: () -> Unit,
  onMarkWished: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(20.dp))
      .clickable { onEdit() }
      .testTag("today_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Avatar
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(item.avatarColor),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.name.take(2).uppercase(),
              style = MaterialTheme.typography.titleMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }

          Column {
            Text(
              text = item.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              item.turningAge?.let { age ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFCE7F3)
                ) {
                  Text(
                    text = "Turning $age 🎉",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFBE185D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = item.zodiacSign,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Language, Relationship tags & Edit button
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Column(horizontalAlignment = Alignment.End) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = item.relationship,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = item.preferredLanguage,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = onEdit,
            modifier = Modifier
              .size(32.dp)
              .testTag("edit_today_btn_${item.id}")
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Contact", tint = Indigo40, modifier = Modifier.size(16.dp))
          }
        }
      }

      if (item.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "💡 ${item.notes}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onSendWish,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1.3f)
            .testTag("wish_now_btn_${item.id}")
        ) {
          Icon(Icons.Default.Celebration, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Wish Now 🎉", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        if (!item.phoneNumber.isNullOrBlank()) {
          OutlinedButton(
            onClick = onWhatsApp,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
            modifier = Modifier.weight(1f)
          ) {
            Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          IconButton(
            onClick = onCall,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = Indigo40, modifier = Modifier.size(18.dp))
          }
        }

        if (item.isWishedThisYear) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Emerald40.copy(alpha = 0.15f)
          ) {
            Text(
              text = "Wished ✓",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Emerald40,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun UpcomingBirthdayCard(
  item: BirthdayItem,
  onEdit: () -> Unit = {},
  onSendWish: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onEdit() }
      .testTag("upcoming_card_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(item.avatarColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = item.name.take(2).uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = item.avatarColor,
            fontWeight = FontWeight.Bold
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = item.name,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFEEF2FF)
            ) {
              val daysText = if (item.daysUntilBirthday == 1) "Tomorrow!" else "In ${item.daysUntilBirthday} days"
              Text(
                text = daysText,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF4F46E5),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }
          Text(
            text = "${item.formattedBirthDate} • ${item.relationship} • ${item.zodiacSign}",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(
          onClick = onEdit,
          modifier = Modifier.size(32.dp).testTag("edit_upcoming_btn_${item.id}")
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Contact", tint = Indigo40, modifier = Modifier.size(16.dp))
        }

        OutlinedButton(
          onClick = onSendWish,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("prep_wish_btn_${item.id}")
        ) {
          Text("Wish", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun OverdueBirthdayCard(
  item: BirthdayItem,
  onEdit: () -> Unit = {},
  onSendBelatedWish: () -> Unit,
  onMarkWished: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onEdit() }
      .testTag("overdue_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(0xFFFEF3C7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
          }

          Column {
            Text(
              text = item.name,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF92400E)
            )
            Text(
              text = "Birthday was ${item.formattedBirthDate} (${item.daysOverdue} days ago)",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFB45309)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFFDE68A)
        ) {
          Text(
            text = "Overdue",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF78350F),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onSendBelatedWish,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
          modifier = Modifier
            .weight(1.5f)
            .testTag("belated_wish_btn_${item.id}")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Send Belated Wish", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onMarkWished,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Mark Wished", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun EmptySectionCard(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

private fun makeCall(context: Context, phone: String) {
  try {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$phone") }
    context.startActivity(intent)
  } catch (_: Exception) {
    Toast.makeText(context, "Could not start phone dialer", Toast.LENGTH_SHORT).show()
  }
}

private fun sendSms(context: Context, phone: String, text: String) {
  try {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("smsto:$phone")
      putExtra("sms_body", text)
    }
    context.startActivity(intent)
  } catch (_: Exception) {
    Toast.makeText(context, "Could not open SMS app", Toast.LENGTH_SHORT).show()
  }
}

private fun openWhatsApp(context: Context, phone: String, text: String) {
  try {
    val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=${Uri.encode(text)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
  } catch (_: Exception) {
    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
  }
}
