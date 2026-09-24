package com.example.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

data class BirthdayItem(
  val id: String,
  val userId: String,
  val name: String,
  val birthMonth: Int, // 1 - 12
  val birthDay: Int,   // 1 - 31
  val birthYear: Int? = null, // Optional for age calculation
  val relationship: String = "Friend", // Family, Friend, Colleague, Partner, VIP, Other
  val phoneNumber: String? = null,
  val preferredLanguage: String = "English", // English, Spanish, French, German, Hindi, Italian
  val notes: String = "",
  val lastWishedYear: Int? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  val isToday: Boolean
    get() {
      val now = LocalDate.now()
      return now.monthValue == birthMonth && now.dayOfMonth == birthDay
    }

  val isWishedThisYear: Boolean
    get() {
      val currentYear = LocalDate.now().year
      return lastWishedYear == currentYear
    }

  private val birthdayThisYearSafe: LocalDate
    get() {
      val today = LocalDate.now()
      return try {
        LocalDate.of(today.year, birthMonth, birthDay)
      } catch (_: Exception) {
        LocalDate.of(today.year, birthMonth, 28)
      }
    }

  /**
   * Overdue means: birthday in the current calendar year occurred already,
   * it occurred within the past 60 days, and the user hasn't marked them as wished this year!
   */
  val isOverdue: Boolean
    get() {
      val today = LocalDate.now()
      if (isToday) return false
      val bdayThisYear = birthdayThisYearSafe
      if (bdayThisYear.isBefore(today)) {
        val daysPassed = ChronoUnit.DAYS.between(bdayThisYear, today).toInt()
        return daysPassed in 1..60 && !isWishedThisYear
      }
      return false
    }

  val daysOverdue: Int
    get() {
      val today = LocalDate.now()
      val bdayThisYear = birthdayThisYearSafe
      return if (bdayThisYear.isBefore(today)) {
        ChronoUnit.DAYS.between(bdayThisYear, today).toInt()
      } else 0
    }

  val daysUntilBirthday: Int
    get() {
      val today = LocalDate.now()
      val birthdayThisYear = birthdayThisYearSafe

      return if (birthdayThisYear.isEqual(today)) {
        0
      } else if (birthdayThisYear.isAfter(today)) {
        ChronoUnit.DAYS.between(today, birthdayThisYear).toInt()
      } else {
        val birthdayNextYear = try {
          LocalDate.of(today.year + 1, birthMonth, birthDay)
        } catch (_: Exception) {
          LocalDate.of(today.year + 1, birthMonth, 28)
        }
        ChronoUnit.DAYS.between(today, birthdayNextYear).toInt()
      }
    }

  val isNext7Days: Boolean
    get() = daysUntilBirthday in 1..7

  val turningAge: Int?
    get() {
      if (birthYear == null || birthYear <= 1900) return null
      val today = LocalDate.now()
      val birthdayThisYear = birthdayThisYearSafe
      val targetYear = if (birthdayThisYear.isBefore(today) && !isToday) today.year else today.year
      val calculated = targetYear - birthYear
      return if (calculated > 0) calculated else null
    }

  val formattedBirthDate: String
    get() {
      val monthName = Month.of(birthMonth.coerceIn(1, 12))
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
      return if (birthYear != null && birthYear > 1900) {
        "$monthName $birthDay, $birthYear"
      } else {
        "$monthName $birthDay"
      }
    }

  val zodiacSign: String
    get() {
      return when (birthMonth) {
        1 -> if (birthDay < 20) "♑ Capricorn" else "♒ Aquarius"
        2 -> if (birthDay < 19) "♒ Aquarius" else "♓ Pisces"
        3 -> if (birthDay < 21) "♓ Pisces" else "♈ Aries"
        4 -> if (birthDay < 20) "♈ Aries" else "♉ Taurus"
        5 -> if (birthDay < 21) "♉ Taurus" else "♊ Gemini"
        6 -> if (birthDay < 21) "♊ Gemini" else "♋ Cancer"
        7 -> if (birthDay < 23) "♋ Cancer" else "♌ Leo"
        8 -> if (birthDay < 23) "♌ Leo" else "♍ Virgo"
        9 -> if (birthDay < 23) "♍ Virgo" else "♎ Libra"
        10 -> if (birthDay < 23) "♎ Libra" else "♏ Scorpio"
        11 -> if (birthDay < 22) "♏ Scorpio" else "♐ Sagittarius"
        12 -> if (birthDay < 22) "♐ Sagittarius" else "♑ Capricorn"
        else -> "✨ Star"
      }
    }

  val avatarColor: Color
    get() {
      val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFFEC4899), // Pink
        Color(0xFF8B5CF6), // Purple
        Color(0xFF0EA5E9), // Sky
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFFEF4444), // Rose
        Color(0xFF14B8A6)  // Teal
      )
      val hash = name.fold(0) { acc, c -> acc + c.code }
      return colors[Math.abs(hash) % colors.size]
    }
}
