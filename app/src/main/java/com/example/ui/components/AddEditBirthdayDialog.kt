package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BirthdayWishesData
import com.example.model.BirthdayItem
import com.example.ui.theme.Indigo40
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBirthdayDialog(
  birthdayToEdit: BirthdayItem? = null,
  initialName: String? = null,
  initialPhone: String? = null,
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    birthMonth: Int,
    birthDay: Int,
    birthYear: Int?,
    relationship: String,
    phoneNumber: String?,
    notes: String,
    preferredLanguage: String
  ) -> Unit
) {
  val context = LocalContext.current
  val today = remember { LocalDate.now() }

  var name by remember { mutableStateOf(birthdayToEdit?.name ?: initialName ?: "") }
  var relationship by remember { mutableStateOf(birthdayToEdit?.relationship ?: "Friend") }
  var birthMonth by remember { mutableIntStateOf(birthdayToEdit?.birthMonth ?: today.monthValue) }
  var birthDay by remember { mutableIntStateOf(birthdayToEdit?.birthDay ?: today.dayOfMonth) }
  var birthYearText by remember { mutableStateOf(birthdayToEdit?.birthYear?.toString() ?: "") }
  var phoneNumber by remember { mutableStateOf(birthdayToEdit?.phoneNumber ?: initialPhone ?: "") }
  var preferredLanguage by remember { mutableStateOf(birthdayToEdit?.preferredLanguage ?: "English") }
  var notes by remember { mutableStateOf(birthdayToEdit?.notes ?: "") }
  var nameError by remember { mutableStateOf<String?>(null) }

  var monthDropdownExpanded by remember { mutableStateOf(false) }

  val relationshipOptions = listOf("Family", "Friend", "Partner", "Colleague", "VIP", "Other")

  val months = remember {
    (1..12).map { m ->
      val mName = Month.of(m).getDisplayName(TextStyle.FULL, Locale.getDefault())
      m to mName
    }
  }

  // System Contact Picker: Pulls contact name & phone number
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
            if (nameIndex != -1) {
              val pulledName = cursor.getString(nameIndex)
              if (!pulledName.isNullOrBlank()) {
                name = pulledName
                nameError = null
              }
            }
            if (numberIndex != -1) {
              val pulledNumber = cursor.getString(numberIndex)
              if (!pulledNumber.isNullOrBlank()) {
                phoneNumber = pulledNumber
              }
            }
            Toast.makeText(context, "Pulled contact details for $name! 📱", Toast.LENGTH_SHORT).show()
          }
        }
      } catch (_: Exception) {
        Toast.makeText(context, "Could not read contact details", Toast.LENGTH_SHORT).show()
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = if (birthdayToEdit != null) Icons.Default.Edit else Icons.Default.Cake,
          contentDescription = null,
          tint = Indigo40
        )
        Text(
          text = if (birthdayToEdit != null) "Edit & Update Contact" else "Add Contact Birthday",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleLarge
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // PULL CONTACT BUTTON (Native Address Book)
        OutlinedButton(
          onClick = {
            val pickIntent = Intent(
              Intent.ACTION_PICK,
              ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            contactPickerLauncher.launch(pickIntent)
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo40),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pull_phone_contact_btn")
        ) {
          Icon(Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Pull from Phone Contacts 📱",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        // Person Name
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            if (it.isNotBlank()) nameError = null
          },
          label = { Text("Contact Full Name *") },
          placeholder = { Text("e.g. Alex Rivera, Mom, Sarah") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          isError = nameError != null,
          supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("birthday_name_input")
        )

        // Phone Number (For WhatsApp, SMS & Direct Call)
        OutlinedTextField(
          value = phoneNumber,
          onValueChange = { phoneNumber = it },
          label = { Text("Phone Number / WhatsApp") },
          placeholder = { Text("e.g. +1 555-0199 or 9876543210") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("birthday_phone_input")
        )

        // Relationship Chips
        Column {
          Text(
            text = "Relationship / Category",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            relationshipOptions.forEach { rel ->
              val isSelected = relationship.equals(rel, ignoreCase = true)
              FilterChip(
                selected = isSelected,
                onClick = { relationship = rel },
                label = { Text(rel, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("chip_rel_$rel")
              )
            }
          }
        }

        // Date selection: Month & Day
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Month Dropdown
          ExposedDropdownMenuBox(
            expanded = monthDropdownExpanded,
            onExpandedChange = { monthDropdownExpanded = !monthDropdownExpanded },
            modifier = Modifier.weight(1.5f)
          ) {
            OutlinedTextField(
              value = Month.of(birthMonth).getDisplayName(TextStyle.SHORT, Locale.getDefault()),
              onValueChange = {},
              readOnly = true,
              label = { Text("Month") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthDropdownExpanded) },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
            )
            ExposedDropdownMenu(
              expanded = monthDropdownExpanded,
              onDismissRequest = { monthDropdownExpanded = false }
            ) {
              months.forEach { (mNum, mName) ->
                DropdownMenuItem(
                  text = { Text(mName) },
                  onClick = {
                    birthMonth = mNum
                    monthDropdownExpanded = false
                  }
                )
              }
            }
          }

          // Day Input
          OutlinedTextField(
            value = birthDay.toString(),
            onValueChange = { input ->
              val parsed = input.filter { it.isDigit() }.toIntOrNull()
              if (parsed != null && parsed in 1..31) {
                birthDay = parsed
              } else if (input.isEmpty()) {
                birthDay = 1
              }
            },
            label = { Text("Day") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("birthday_day_input")
          )
        }

        // Birth Year (Optional)
        OutlinedTextField(
          value = birthYearText,
          onValueChange = { input ->
            if (input.length <= 4 && input.all { it.isDigit() }) {
              birthYearText = input
            }
          },
          label = { Text("Birth Year (Optional)") },
          placeholder = { Text("e.g. 1998 (calculates turning age)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("birthday_year_input")
        )

        // Preferred Wish Language
        Column {
          Text(
            text = "Wish Language Preference",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            BirthdayWishesData.languages.forEach { lang ->
              val isSelected = preferredLanguage.equals(lang, ignoreCase = true)
              FilterChip(
                selected = isSelected,
                onClick = { preferredLanguage = lang },
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                label = { Text(lang, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = Indigo40.copy(alpha = 0.15f),
                  selectedLabelColor = Indigo40
                )
              )
            }
          }
        }

        // Notes & Gift Ideas
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Gift Ideas & Notes") },
          placeholder = { Text("e.g. Loves sci-fi books, photography...") },
          leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
          maxLines = 3,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("birthday_notes_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank()) {
            nameError = "Please enter contact name"
            return@Button
          }
          val parsedYear = birthYearText.toIntOrNull()
          onSave(
            name.trim(),
            birthMonth,
            birthDay,
            parsedYear,
            relationship,
            phoneNumber.trim().ifBlank { null },
            notes.trim(),
            preferredLanguage
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("save_birthday_btn")
      ) {
        Text(
          text = if (birthdayToEdit != null) "Update Contact" else "Save Contact",
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
