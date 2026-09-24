package com.example.ui.screens.birthday

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BirthdayWishesData
import com.example.model.BirthdayItem
import com.example.ui.TaskViewModel
import com.example.ui.components.CardTheme
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayWishStationSection(
  viewModel: TaskViewModel,
  preselectedContact: BirthdayItem? = null
) {
  val context = LocalContext.current
  val allBirthdays by viewModel.allBirthdays.collectAsState()

  var selectedContact by remember(preselectedContact) {
    mutableStateOf(preselectedContact ?: allBirthdays.firstOrNull())
  }
  var recipientName by remember(selectedContact) {
    mutableStateOf(selectedContact?.name ?: "Friend")
  }
  var recipientPhone by remember(selectedContact) {
    mutableStateOf(selectedContact?.phoneNumber ?: "")
  }
  var selectedLanguage by remember(selectedContact) {
    mutableStateOf(selectedContact?.preferredLanguage ?: "English")
  }
  var selectedCategory by remember(selectedContact) {
    mutableStateOf(if (selectedContact?.isOverdue == true) "Belated Wishes" else "Warm & Heartfelt")
  }
  var selectedCardTheme by remember { mutableStateOf(CardTheme.PARTY) }
  var recipientDropdownExpanded by remember { mutableStateOf(false) }

  // Initial message formatting
  val initialWish = remember(selectedContact, selectedCategory, selectedLanguage) {
    val templates = BirthdayWishesData.getTemplatesFor(selectedCategory, selectedLanguage)
    val template = templates.firstOrNull()?.template
      ?: "Happy Birthday, {name}! 🎉 Wishing you a day filled with joy, laughter, and success!"
    BirthdayWishesData.formatWish(template, recipientName, selectedContact?.turningAge)
  }
  var wishMessage by remember(initialWish) { mutableStateOf(initialWish) }

  val availableTemplates = remember(selectedCategory, selectedLanguage, recipientName, selectedContact) {
    val list = BirthdayWishesData.getTemplatesFor(selectedCategory, selectedLanguage)
    list.map { BirthdayWishesData.formatWish(it.template, recipientName, selectedContact?.turningAge) }
  }

  fun copyToClipboard(text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Birthday Wish", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Wish copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
  }

  fun shareViaChooser(text: String) {
    val intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Send Birthday Wish via..."))
  }

  fun sendWhatsApp(phone: String, text: String) {
    try {
      val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=${Uri.encode(text)}")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      context.startActivity(intent)
    } catch (_: Exception) {
      shareViaChooser(text)
    }
  }

  fun sendSms(phone: String, text: String) {
    try {
      val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phone")
        putExtra("sms_body", text)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      shareViaChooser(text)
    }
  }

  fun makeCall(phone: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$phone") }
      context.startActivity(intent)
    } catch (_: Exception) {
      Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("wish_station_lazy_column"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp, top = 8.dp)
  ) {
    // Header
    item {
      Text(
        text = "Wish Station & Cards",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Generate personalized wishes, greeting cards & send via WhatsApp or SMS.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Recipient Selector Dropdown
    item {
      if (allBirthdays.isNotEmpty()) {
        ExposedDropdownMenuBox(
          expanded = recipientDropdownExpanded,
          onExpandedChange = { recipientDropdownExpanded = !recipientDropdownExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedContact?.name ?: recipientName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Recipient Contact") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recipientDropdownExpanded) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("recipient_selector_field")
          )
          ExposedDropdownMenu(
            expanded = recipientDropdownExpanded,
            onDismissRequest = { recipientDropdownExpanded = false }
          ) {
            allBirthdays.forEach { contact ->
              DropdownMenuItem(
                text = {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(contact.name, fontWeight = FontWeight.SemiBold)
                    Text(
                      text = if (contact.isToday) "🎉 Today" else contact.formattedBirthDate,
                      fontSize = 11.sp,
                      color = if (contact.isToday) Color(0xFFEC4899) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                },
                onClick = {
                  selectedContact = contact
                  recipientName = contact.name
                  recipientPhone = contact.phoneNumber ?: ""
                  selectedLanguage = contact.preferredLanguage
                  if (contact.isOverdue) selectedCategory = "Belated Wishes"
                  recipientDropdownExpanded = false
                }
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      } else {
        OutlinedTextField(
          value = recipientName,
          onValueChange = { recipientName = it },
          label = { Text("Recipient Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }

    // Language Selector Chips
    item {
      Text(
        text = "Wish Language:",
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
          val isSelected = selectedLanguage == lang
          FilterChip(
            selected = isSelected,
            onClick = { selectedLanguage = lang },
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp)) },
            label = { Text(lang, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Indigo40.copy(alpha = 0.15f),
              selectedLabelColor = Indigo40
            ),
            modifier = Modifier.testTag("wish_lang_$lang")
          )
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Category Selector Chips
    item {
      Text(
        text = "Tone & Category:",
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
        BirthdayWishesData.categories.forEach { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("station_cat_$cat")
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // LIVE GREETING CARD PREVIEW
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Palette, contentDescription = null, tint = Indigo40, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Live Greeting Card Preview",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          // Theme Selector Chips
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CardTheme.entries.forEach { theme ->
              val isSelected = selectedCardTheme == theme
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Indigo40 else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedCardTheme = theme }
                  .testTag("station_theme_${theme.name.lowercase()}")
              ) {
                Text(
                  text = theme.displayName.split(" ").first(),
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // The Greeting Card
        Card(
          shape = RoundedCornerShape(22.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("live_greeting_card")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Brush.linearGradient(selectedCardTheme.gradient))
              .padding(20.dp)
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "✨ 🎂 ✨",
                fontSize = 22.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Happy Birthday, $recipientName!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = selectedCardTheme.textColor,
                textAlign = TextAlign.Center
              )

              selectedContact?.turningAge?.let { age ->
                Text(
                  text = "🎉 Celebrating $age Wonderful Years 🎉",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = selectedCardTheme.accentColor
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = wishMessage,
                  style = MaterialTheme.typography.bodyMedium,
                  fontStyle = FontStyle.Italic,
                  color = selectedCardTheme.textColor,
                  textAlign = TextAlign.Center,
                  lineHeight = 20.sp,
                  modifier = Modifier.padding(14.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "— Sent with Warm Wishes & Love —",
                style = MaterialTheme.typography.labelSmall,
                color = selectedCardTheme.textColor.copy(alpha = 0.75f),
                fontSize = 10.sp
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }
    }

    // Quick Templates Carousel
    item {
      Text(
        text = "Quick Wish Suggestions (Tap to use):",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        availableTemplates.take(8).forEachIndexed { idx, tpl ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (wishMessage == tpl) Indigo40 else Color.Transparent
            ),
            modifier = Modifier
              .width(220.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable { wishMessage = tpl }
              .testTag("wish_station_template_$idx")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = tpl,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                maxLines = 3,
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap to apply 👆",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = Indigo40,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Editable Message Box
    item {
      OutlinedTextField(
        value = wishMessage,
        onValueChange = { wishMessage = it },
        label = { Text("Personalize Wish Message") },
        leadingIcon = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
        maxLines = 6,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("station_message_editor")
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // One-Tap Delivery Actions
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Native Share Sheet (Works everywhere: WhatsApp, Instagram, Telegram, Email)
        Button(
          onClick = {
            shareViaChooser(wishMessage)
            selectedContact?.let { viewModel.markBirthdayWished(it) }
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("station_share_btn")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Share Greeting (WhatsApp / Social / SMS)", fontWeight = FontWeight.Bold)
        }

        // Action Buttons Row: WhatsApp, SMS, Call, Copy
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Direct WhatsApp
          if (recipientPhone.isNotBlank()) {
            OutlinedButton(
              onClick = {
                sendWhatsApp(recipientPhone, wishMessage)
                selectedContact?.let { viewModel.markBirthdayWished(it) }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
              modifier = Modifier
                .weight(1.2f)
                .testTag("station_whatsapp_btn")
            ) {
              Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Direct SMS
            OutlinedButton(
              onClick = {
                sendSms(recipientPhone, wishMessage)
                selectedContact?.let { viewModel.markBirthdayWished(it) }
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("station_sms_btn")
            ) {
              Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("SMS", fontSize = 11.sp)
            }

            // Call
            OutlinedButton(
              onClick = { makeCall(recipientPhone) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("station_call_btn")
            ) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Call", fontSize = 11.sp)
            }
          }

          // Copy Button
          OutlinedButton(
            onClick = { copyToClipboard(wishMessage) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("station_copy_btn")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Copy", fontSize = 11.sp)
          }
        }

        // Mark Wished
        selectedContact?.let { contact ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (contact.isWishedThisYear) Emerald40.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { viewModel.markBirthdayWished(contact) }
              .padding(vertical = 10.dp, horizontal = 14.dp)
              .testTag("station_mark_wished_btn")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (contact.isWishedThisYear) "Marked as Wished for 2026 ✓" else "Mark as Wished for 2026",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (contact.isWishedThisYear) Emerald40 else MaterialTheme.colorScheme.onSurface
              )
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (contact.isWishedThisYear) Emerald40 else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
