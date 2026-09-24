package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.components.AddEditBirthdayDialog
import com.example.ui.components.SendBirthdayWishDialog
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40
import com.example.ui.theme.Indigo60
import com.example.ui.theme.Sky40

@Composable
fun BirthdayWishingScreen(
  viewModel: TaskViewModel,
  onOpenAddBirthday: () -> Unit
) {
  val context = LocalContext.current
  val filteredBirthdays by viewModel.filteredBirthdays.collectAsState()
  val allBirthdays by viewModel.allBirthdays.collectAsState()
  val todaysBirthdays by viewModel.todaysBirthdays.collectAsState()
  val upcomingBirthdays by viewModel.upcomingBirthdays.collectAsState()
  val filterCategory by viewModel.birthdayFilterCategory.collectAsState()
  val searchQuery by viewModel.birthdaySearchQuery.collectAsState()

  var selectedBirthdayForWish by remember { mutableStateOf<BirthdayItem?>(null) }
  var birthdayToEdit by remember { mutableStateOf<BirthdayItem?>(null) }

  val filterCategories = listOf("All", "Today 🎉", "Upcoming", "Family", "Friends", "Work")

  Column(modifier = Modifier.fillMaxSize()) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { viewModel.birthdaySearchQuery.value = it },
      placeholder = { Text("Search by name, relationship, notes...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { viewModel.birthdaySearchQuery.value = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .testTag("birthday_search_field")
    )

    // Today's Celebration Hero Banner (if someone's birthday is today)
    if (todaysBirthdays.isNotEmpty()) {
      val firstToday = todaysBirthdays.first()
      Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("today_birthday_hero_card")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFEC4899))
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(20.dp))
                Text(
                  text = "TODAY'S CELEBRATION!",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFFFFD166),
                  letterSpacing = 1.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${firstToday.name}'s Birthday! 🎂",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              firstToday.turningAge?.let { age ->
                Text(
                  text = "Celebrating $age fabulous years today!",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f)
                )
              }
            }

            Button(
              onClick = { selectedBirthdayForWish = firstToday },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Indigo40
              ),
              modifier = Modifier.testTag("hero_wish_now_btn")
            ) {
              Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Wish Now", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      // Summary Overview Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${allBirthdays.size}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Indigo60
            )
            Text("Total Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Box(modifier = Modifier.height(30.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${todaysBirthdays.size}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Emerald40
            )
            Text("Today's", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Box(modifier = Modifier.height(30.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${upcomingBirthdays.size}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Sky40
            )
            Text("Next 7 Days", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }

    // Filter Chips Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      filterCategories.forEach { cat ->
        val isSelected = filterCategory == cat
        FilterChip(
          selected = isSelected,
          onClick = { viewModel.birthdayFilterCategory.value = cat },
          label = { Text(cat, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("filter_bday_$cat")
        )
      }
    }

    // Birthday List
    if (filteredBirthdays.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 64.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Cake,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (searchQuery.isNotEmpty()) "No birthdays match your search" else "No birthdays in this section",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Keep track of friends, family, and loved ones never missing a wish!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onOpenAddBirthday,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
            modifier = Modifier.testTag("empty_add_birthday_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add a Birthday")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(
          items = filteredBirthdays,
          key = { it.id }
        ) { birthday ->
          BirthdayCardItem(
            birthday = birthday,
            onWishClick = { selectedBirthdayForWish = birthday },
            onEditClick = { birthdayToEdit = birthday },
            onDeleteClick = { viewModel.deleteBirthday(birthday) },
            onMarkWished = { viewModel.markBirthdayWished(birthday) }
          )
        }
      }
    }
  }

  // Send Birthday Wish Dialog
  selectedBirthdayForWish?.let { bday ->
    SendBirthdayWishDialog(
      birthday = bday,
      onDismiss = { selectedBirthdayForWish = null },
      onMarkWished = {
        viewModel.markBirthdayWished(bday)
      }
    )
  }

  // Edit Birthday Dialog
  birthdayToEdit?.let { bday ->
    AddEditBirthdayDialog(
      birthdayToEdit = bday,
      onDismiss = { birthdayToEdit = null },
      onSave = { name, month, day, year, relationship, phone, notes, language ->
        viewModel.updateBirthday(
          bday.copy(
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
        birthdayToEdit = null
      }
    )
  }
}

@Composable
fun BirthdayCardItem(
  birthday: BirthdayItem,
  onWishClick: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onMarkWished: () -> Unit
) {
  val context = LocalContext.current
  var menuExpanded by remember { mutableStateOf(false) }

  val daysUntil = birthday.daysUntilBirthday

  val badgeColor = when {
    birthday.isToday -> Emerald40
    daysUntil == 1 -> Sky40
    daysUntil <= 7 -> Indigo60
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }

  val badgeText = when {
    birthday.isToday -> "🎉 TODAY!"
    daysUntil == 1 -> "Tomorrow 🎈"
    else -> "In $daysUntil days"
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (birthday.isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (birthday.isToday) 3.dp else 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("birthday_card_${birthday.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Avatar circle + Name & details
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                if (birthday.isToday) Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))
                else Brush.linearGradient(listOf(Indigo40.copy(alpha = 0.8f), Indigo60))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = (birthday.name.firstOrNull() ?: 'B').uppercase(),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = birthday.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = birthday.relationship,
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "${birthday.formattedBirthDate} • ${birthday.zodiacSign}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            birthday.turningAge?.let { age ->
              Text(
                text = if (birthday.isToday) "Turning $age today! 🎂" else "Turns $age",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Indigo60
              )
            }
          }
        }

        // Days Countdown Badge & Menu
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = badgeColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = badgeColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier.size(32.dp).testTag("birthday_menu_${birthday.id}")
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Options", modifier = Modifier.size(16.dp))
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Edit Birthday") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                  menuExpanded = false
                  onEditClick()
                }
              )
              DropdownMenuItem(
                text = { Text(if (birthday.isWishedThisYear) "Marked as Wished ✓" else "Mark as Wished") },
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                onClick = {
                  menuExpanded = false
                  onMarkWished()
                }
              )
              DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                  menuExpanded = false
                  onDeleteClick()
                }
              )
            }
          }
        }
      }

      // Notes / Gift Ideas
      if (birthday.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "💡 ${birthday.notes}",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons Row: Wish Now, SMS, Call
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (birthday.isWishedThisYear) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald40, modifier = Modifier.size(16.dp))
            Text("Wished for 2026", style = MaterialTheme.typography.labelSmall, color = Emerald40, fontWeight = FontWeight.Bold)
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Quick call button if phone present
          if (!birthday.phoneNumber.isNullOrBlank()) {
            IconButton(
              onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:${birthday.phoneNumber}")
                }
                context.startActivity(intent)
              },
              modifier = Modifier.size(34.dp).testTag("card_call_btn_${birthday.id}")
            ) {
              Icon(Icons.Default.Call, contentDescription = "Call", tint = Indigo40, modifier = Modifier.size(18.dp))
            }

            IconButton(
              onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                  data = Uri.parse("smsto:${birthday.phoneNumber}")
                }
                context.startActivity(intent)
              },
              modifier = Modifier.size(34.dp).testTag("card_sms_btn_${birthday.id}")
            ) {
              Icon(Icons.Default.Message, contentDescription = "SMS", tint = Indigo40, modifier = Modifier.size(18.dp))
            }
          }

          // Wish Now button
          Button(
            onClick = onWishClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("card_wish_btn_${birthday.id}")
          ) {
            Icon(Icons.Default.Cake, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Wish", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
