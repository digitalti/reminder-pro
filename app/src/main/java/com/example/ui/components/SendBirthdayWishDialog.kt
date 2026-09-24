package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.Emerald40
import com.example.ui.theme.Indigo40
import com.example.ui.theme.Indigo60
import com.example.ui.theme.Sky40

enum class CardTheme(
  val displayName: String,
  val gradient: List<Color>,
  val textColor: Color,
  val accentColor: Color
) {
  PARTY(
    "Party Confetti",
    listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFEC4899)),
    Color.White,
    Color(0xFFFFD166)
  ),
  ROYAL_GOLD(
    "Royal Amber",
    listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF78350F)),
    Color(0xFFFDE68A),
    Color(0xFFF59E0B)
  ),
  PASTEL_BLOOM(
    "Pastel Rose",
    listOf(Color(0xFFFCE7F3), Color(0xFFEDE9FE), Color(0xFFE0E7FF)),
    Color(0xFF4338CA),
    Color(0xFFBE185D)
  ),
  SUNSET(
    "Sunset Glow",
    listOf(Color(0xFFF97316), Color(0xFFFB7185), Color(0xFFA855F7)),
    Color.White,
    Color(0xFFFFE066)
  )
}

@Composable
fun SendBirthdayWishDialog(
  birthday: BirthdayItem,
  onDismiss: () -> Unit,
  onMarkWished: () -> Unit
) {
  val context = LocalContext.current

  var selectedCategory by remember {
    mutableStateOf(if (birthday.isOverdue) "Belated Wishes" else "All")
  }
  var selectedLanguage by remember {
    mutableStateOf(birthday.preferredLanguage)
  }
  var selectedCardTheme by remember { mutableStateOf(CardTheme.PARTY) }

  // Initial wish message
  val defaultWish = remember(birthday) {
    val sample = if (birthday.isOverdue) {
      BirthdayWishesData.templates.firstOrNull { it.category == "Belated Wishes" }?.template
        ?: "Happy Belated Birthday, {name}! Wishing you joy and happiness!"
    } else {
      BirthdayWishesData.templates.firstOrNull()?.template
        ?: "Happy Birthday, {name}! Wishing you joy and happiness!"
    }
    BirthdayWishesData.formatWish(sample, birthday.name, birthday.turningAge)
  }
  var wishMessage by remember { mutableStateOf(defaultWish) }
  var hasMarkedWished by remember { mutableStateOf(birthday.isWishedThisYear) }

  val filteredTemplates = remember(selectedCategory, selectedLanguage, birthday.name) {
    val list = BirthdayWishesData.getTemplatesFor(selectedCategory, selectedLanguage)
    list.map { BirthdayWishesData.formatWish(it.template, birthday.name, birthday.turningAge) }
  }

  fun shareWishViaIntent(text: String) {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
    }
    val chooser = Intent.createChooser(sendIntent, "Send Birthday Wish via...")
    try {
      context.startActivity(chooser)
    } catch (_: Exception) {
      Toast.makeText(context, "Could not open sharing app", Toast.LENGTH_SHORT).show()
    }
  }

  fun copyToClipboard(text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Birthday Wish", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Wish copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
  }

  fun sendDirectSms(phone: String, text: String) {
    try {
      val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phone")
        putExtra("sms_body", text)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      shareWishViaIntent(text)
    }
  }

  fun sendDirectWhatsApp(phone: String, text: String) {
    try {
      val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(text)}")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      context.startActivity(intent)
    } catch (_: Exception) {
      shareWishViaIntent(text)
    }
  }

  fun makePhoneCall(phone: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      Toast.makeText(context, "Could not start phone dialer", Toast.LENGTH_SHORT).show()
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier.fillMaxWidth(),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Indigo40),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Cake,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Column {
            Text(
              text = "Wish ${birthday.name}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (birthday.isToday) "🎉 Birthday Today!" else "🎂 Birthday in ${birthday.daysUntilBirthday} days",
              style = MaterialTheme.typography.labelSmall,
              color = if (birthday.isToday) Indigo40 else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Person Info & Milestones Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${birthday.relationship} • ${birthday.zodiacSign}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              birthday.turningAge?.let { age ->
                Text(
                  text = "Turning $age years old! 🎈",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = Indigo60
                )
              }
            }

            if (hasMarkedWished) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Emerald40.copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald40, modifier = Modifier.size(14.dp))
                  Text("Wished ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald40)
                }
              }
            }
          }
        }

        // Live Visual Greeting Card Preview
        Card(
          shape = RoundedCornerShape(18.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_greeting_card")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Brush.linearGradient(selectedCardTheme.gradient))
              .padding(16.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "✨ HAPPY BIRTHDAY ✨",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = selectedCardTheme.accentColor,
                letterSpacing = 1.5.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = birthday.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = selectedCardTheme.textColor,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "\"$wishMessage\"",
                style = MaterialTheme.typography.bodyMedium,
                color = selectedCardTheme.textColor.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontStyle = FontStyle.Italic
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "🎈 May all your wishes come true! 🎂",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = selectedCardTheme.accentColor
              )
            }
          }
        }

        // Card Theme Switcher
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Palette, contentDescription = "Card Themes", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
          Text("Card Style:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            CardTheme.values().forEach { theme ->
              val isSelected = selectedCardTheme == theme
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Indigo40 else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedCardTheme = theme }
                  .testTag("theme_${theme.name.lowercase()}")
              ) {
                Text(
                  text = theme.displayName,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        // Language Selector Chips
        Column {
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
                label = { Text(lang, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = Indigo40.copy(alpha = 0.2f),
                  selectedLabelColor = Indigo40
                )
              )
            }
          }
        }

        // Wish Category Chips
        Column {
          Text(
            text = "Pick a Wish Template:",
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
                modifier = Modifier.testTag("wish_cat_$cat")
              )
            }
          }
        }

        // Horizontal Quick Wish Suggestions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          filteredTemplates.take(6).forEachIndexed { index, templateText ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (wishMessage == templateText) Indigo40 else Color.Transparent
              ),
              modifier = Modifier
                .width(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { wishMessage = templateText }
                .testTag("template_card_$index")
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = templateText,
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 3,
                  fontSize = 12.sp,
                  lineHeight = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Tap to use 👆",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Indigo40
                )
              }
            }
          }
        }

        // Editable Custom Message Box
        OutlinedTextField(
          value = wishMessage,
          onValueChange = { wishMessage = it },
          label = { Text("Personalize Message") },
          leadingIcon = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
          maxLines = 5,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("wish_message_editor")
        )

        // Action Buttons Row: WhatsApp / Share, SMS, Call, Copy
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Primary Share Button
          Button(
            onClick = {
              shareWishViaIntent(wishMessage)
              if (!hasMarkedWished) {
                hasMarkedWished = true
                onMarkWished()
              }
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo40),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("share_wish_btn")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Wish (WhatsApp / Apps)", fontWeight = FontWeight.Bold)
          }

          // Secondary Quick Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // WhatsApp button if phone available
            if (!birthday.phoneNumber.isNullOrBlank()) {
              OutlinedButton(
                onClick = {
                  sendDirectWhatsApp(birthday.phoneNumber, wishMessage)
                  if (!hasMarkedWished) {
                    hasMarkedWished = true
                    onMarkWished()
                  }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                modifier = Modifier
                  .weight(1.1f)
                  .testTag("whatsapp_wish_btn")
              ) {
                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Copy button
            OutlinedButton(
              onClick = { copyToClipboard(wishMessage) },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("copy_wish_btn")
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Copy", fontSize = 12.sp)
            }

            // Direct SMS if phone available
            if (!birthday.phoneNumber.isNullOrBlank()) {
              OutlinedButton(
                onClick = {
                  sendDirectSms(birthday.phoneNumber, wishMessage)
                  if (!hasMarkedWished) {
                    hasMarkedWished = true
                    onMarkWished()
                  }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("sms_wish_btn")
              ) {
                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SMS", fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = { makePhoneCall(birthday.phoneNumber) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("call_contact_btn")
              ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Call", fontSize = 12.sp)
              }
            }
          }

          // Mark as Wished toggle
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasMarkedWished) Emerald40.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                hasMarkedWished = !hasMarkedWished
                if (hasMarkedWished) onMarkWished()
              }
              .padding(vertical = 8.dp, horizontal = 12.dp)
              .testTag("toggle_mark_wished_btn")
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (hasMarkedWished) "Marked as Wished for 2026 🎉" else "Mark as Wished for this year",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (hasMarkedWished) FontWeight.Bold else FontWeight.Normal,
                color = if (hasMarkedWished) Emerald40 else MaterialTheme.colorScheme.onSurface
              )
              Icon(
                imageVector = if (hasMarkedWished) Icons.Default.CheckCircle else Icons.Default.ThumbUp,
                contentDescription = null,
                tint = if (hasMarkedWished) Emerald40 else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("close_wish_dialog_btn")
      ) {
        Text("Done", fontWeight = FontWeight.Bold)
      }
    }
  )
}
