package com.example.ui.screens.birthday

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.material3.OutlinedButton
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

enum class ContactSortOrder(val label: String) {
  UPCOMING("Next Upcoming"),
  NAME("Name (A-Z)"),
  AGE("Age / Birthdate")
}

@Composable
fun BirthdayContactsSection(
  viewModel: TaskViewModel,
  onOpenAddBirthday: () -> Unit,
  onOpenAddBirthdayWithContact: (name: String, phone: String?) -> Unit = { _, _ -> onOpenAddBirthday() },
  onEditBirthday: (BirthdayItem) -> Unit,
  onSendWish: (BirthdayItem) -> Unit
) {
  val context = LocalContext.current
  val allBirthdays by viewModel.allBirthdays.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var sortOrder by remember { mutableStateOf(ContactSortOrder.UPCOMING) }
  var sortMenuExpanded by remember { mutableStateOf(false) }

  val categories = listOf("All", "Family", "Friend", "Colleague", "VIP", "Partner", "Other")

  // Contact Picker Launcher
  val contactPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val contactUri = result.data?.data ?: return@rememberLauncherForActivityResult
      try {
        val projection = arrayOf(
          ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
          ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        context.contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
          if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val pulledName = if (nameIndex != -1) cursor.getString(nameIndex) else "Friend"
            val pulledNumber = if (numberIndex != -1) cursor.getString(numberIndex) else null
            onOpenAddBirthdayWithContact(pulledName ?: "Friend", pulledNumber)
          }
        }
      } catch (_: Exception) {
        Toast.makeText(context, "Could not pull contact details", Toast.LENGTH_SHORT).show()
      }
    }
  }

  val filteredList = remember(allBirthdays, searchQuery, selectedCategory, sortOrder) {
    val filtered = allBirthdays.filter { item ->
      val matchesCat = selectedCategory == "All" || item.relationship.equals(selectedCategory, ignoreCase = true)
      val matchesQuery = searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.notes.contains(searchQuery, ignoreCase = true) ||
        (item.phoneNumber?.contains(searchQuery) == true) ||
        item.preferredLanguage.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }

    when (sortOrder) {
      ContactSortOrder.UPCOMING -> filtered.sortedBy { it.daysUntilBirthday }
      ContactSortOrder.NAME -> filtered.sortedBy { it.name.lowercase() }
      ContactSortOrder.AGE -> filtered.sortedBy { it.birthYear ?: 9999 }
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("contacts_lazy_column"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Top Bar Header & Controls
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
              text = "Contacts Directory",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${filteredList.size} contacts saved",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Sort Button
          Box {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { sortMenuExpanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("sort_contacts_btn")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(sortOrder.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
            }

            DropdownMenu(
              expanded = sortMenuExpanded,
              onDismissRequest = { sortMenuExpanded = false }
            ) {
              ContactSortOrder.entries.forEach { order ->
                DropdownMenuItem(
                  text = { Text(order.label) },
                  onClick = {
                    sortOrder = order
                    sortMenuExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pull from Phone Contacts Quick Action Banner
        OutlinedButton(
          onClick = {
            val pickIntent = Intent(
              Intent.ACTION_PICK,
              ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            contactPickerLauncher.launch(pickIntent)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo40),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pull_contact_banner_btn")
        ) {
          Icon(Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Pull Name & Phone from Contacts 📱", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Field
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, phone, language, notes...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("contacts_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Indigo40.copy(alpha = 0.15f),
                selectedLabelColor = Indigo40
              ),
              modifier = Modifier.testTag("contact_cat_$cat")
            )
          }
        }
      }
    }

    // Contacts List
    if (filteredList.isEmpty()) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 20.dp, end = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Default.Cake, contentDescription = null, tint = Indigo40, modifier = Modifier.size(56.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (searchQuery.isNotBlank()) "No matching contacts found" else "No contacts in directory yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = if (searchQuery.isNotBlank()) "Try searching for a different name or relationship." else "Pull contacts directly from your phone's address book or add manually.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val pickIntent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
              )
              contactPickerLauncher.launch(pickIntent)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo40)
          ) {
            Icon(Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pull from Phone Contacts 📱")
          }
        }
      }
    } else {
      items(filteredList, key = { it.id }) { item ->
        ContactDetailCard(
          item = item,
          onSendWish = { onSendWish(item) },
          onEdit = { onEditBirthday(item) },
          onDelete = { viewModel.deleteBirthday(item) },
          onMarkWished = { viewModel.markBirthdayWished(item) },
          onCall = { item.phoneNumber?.let { p -> makeCall(context, p) } },
          onSms = { item.phoneNumber?.let { p -> sendSms(context, p, "Happy Birthday, ${item.name}! 🎂🎉") } },
          onWhatsApp = { item.phoneNumber?.let { p -> openWhatsApp(context, p, "Happy Birthday, ${item.name}! 🎂🎉") } }
        )
      }
    }
  }
}

@Composable
private fun ContactDetailCard(
  item: BirthdayItem,
  onSendWish: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onMarkWished: () -> Unit,
  onCall: () -> Unit,
  onSms: () -> Unit,
  onWhatsApp: () -> Unit
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .clickable { onEdit() }
      .testTag("contact_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Initials Avatar
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(item.avatarColor),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.name.take(2).uppercase(),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              if (item.isToday) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(0xFFFCE7F3)
                ) {
                  Text(
                    text = "TODAY! 🎂",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color(0xFFBE185D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }

            Text(
              text = "${item.formattedBirthDate} • ${item.zodiacSign}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.turningAge != null) {
              Text(
                text = "Turning ${item.turningAge} years old",
                style = MaterialTheme.typography.labelSmall,
                color = Indigo40,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        // Action Buttons: Direct Edit Button + Three Dots Menu
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onEdit,
            modifier = Modifier
              .size(34.dp)
              .testTag("edit_contact_btn_${item.id}")
          ) {
            Icon(
              Icons.Default.Edit,
              contentDescription = "Edit Contact",
              tint = Indigo40,
              modifier = Modifier.size(18.dp)
            )
          }

          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier.size(34.dp)
            ) {
              Icon(
                Icons.Default.MoreVert,
                contentDescription = "More actions",
                modifier = Modifier.size(18.dp)
              )
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Edit Details") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                  menuExpanded = false
                  onEdit()
                }
              )
              DropdownMenuItem(
                text = { Text("Send Birthday Wish") },
                leadingIcon = { Icon(Icons.Default.Celebration, contentDescription = null, tint = Indigo40) },
                onClick = {
                  menuExpanded = false
                  onSendWish()
                }
              )
              DropdownMenuItem(
                text = { Text("Mark Wished for 2026") },
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald40) },
                onClick = {
                  menuExpanded = false
                  onMarkWished()
                }
              )
              DropdownMenuItem(
                text = { Text("Delete Contact", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                  menuExpanded = false
                  onDelete()
                }
              )
            }
          }
        }
      }

      // Badges: Relationship, Language, Wished Status
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        ) {
          Text(
            text = item.relationship,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = item.preferredLanguage,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (item.isWishedThisYear) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Emerald40.copy(alpha = 0.15f)
          ) {
            Text(
              text = "Wished ✓",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Emerald40,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Phone & Quick Contact Actions
      if (!item.phoneNumber.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = item.phoneNumber,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // WhatsApp
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF25D366).copy(alpha = 0.12f),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onWhatsApp() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
            }
            // SMS
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSms() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("SMS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
            // Call
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Indigo40.copy(alpha = 0.12f),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onCall() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("Call", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Indigo40)
            }
          }
        }
      }

      // Notes
      if (item.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = item.notes,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
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
