package com.example.data

import com.example.data.local.BirthdayDao
import com.example.data.local.BirthdayEntity
import com.example.model.BirthdayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class BirthdayRepository(
  private val birthdayDao: BirthdayDao
) {
  fun getBirthdaysForUser(userId: String): Flow<List<BirthdayItem>> {
    return birthdayDao.getBirthdaysForUser(userId).map { entities ->
      entities.map { it.toModel() }
    }
  }

  suspend fun insertOrUpdate(item: BirthdayItem) {
    val entity = BirthdayEntity.fromModel(item)
    birthdayDao.insertBirthday(entity)
  }

  suspend fun deleteBirthday(id: String, userId: String) {
    birthdayDao.deleteBirthday(id, userId)
  }

  suspend fun markAsWished(id: String, userId: String, year: Int) {
    birthdayDao.markAsWished(id, userId, year, System.currentTimeMillis())
  }

  suspend fun seedSampleBirthdays(userId: String) {
    val today = LocalDate.now()
    val fourDaysAgo = today.minusDays(4)
    val twelveDaysAgo = today.minusDays(12)
    val inTwoDays = today.plusDays(2)
    val inFiveDays = today.plusDays(5)
    val nextMonth = today.plusMonths(1)

    val samples = listOf(
      // TODAY'S BIRTHDAY
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Alex Rivera",
        birthMonth = today.monthValue,
        birthDay = today.dayOfMonth,
        birthYear = today.year - 26,
        relationship = "Friend",
        phoneNumber = "+1234567890",
        preferredLanguage = "English",
        notes = "Loves matcha latte and vintage photography 📸",
        lastWishedYear = null
      ),
      // NEXT 7 DAYS - 1
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Elena Rostova",
        birthMonth = inTwoDays.monthValue,
        birthDay = inTwoDays.dayOfMonth,
        birthYear = today.year - 24,
        relationship = "Family",
        phoneNumber = "+1987654321",
        preferredLanguage = "English",
        notes = "Order fresh lilies and send morning call 💐",
        lastWishedYear = null
      ),
      // NEXT 7 DAYS - 2
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Carlos Gomez",
        birthMonth = inFiveDays.monthValue,
        birthDay = inFiveDays.dayOfMonth,
        birthYear = today.year - 30,
        relationship = "Colleague",
        phoneNumber = "+34612345678",
        preferredLanguage = "Spanish",
        notes = "Team lunch celebration at 1 PM 🍕",
        lastWishedYear = null
      ),
      // OVERDUE - Missed 4 days ago
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Sophia Chen",
        birthMonth = fourDaysAgo.monthValue,
        birthDay = fourDaysAgo.dayOfMonth,
        birthYear = today.year - 28,
        relationship = "VIP",
        phoneNumber = "+14155552671",
        preferredLanguage = "English",
        notes = "Send Belated warm greeting and coffee voucher ☕",
        lastWishedYear = null
      ),
      // OVERDUE - Missed 12 days ago
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Marcus Vance",
        birthMonth = twelveDaysAgo.monthValue,
        birthDay = twelveDaysAgo.dayOfMonth,
        birthYear = today.year - 32,
        relationship = "Friend",
        phoneNumber = "+12025550199",
        preferredLanguage = "English",
        notes = "Catch up over weekend dinner 🍔",
        lastWishedYear = null
      ),
      // UPCOMING LATER
      BirthdayItem(
        id = UUID.randomUUID().toString(),
        userId = userId,
        name = "Aarav Sharma",
        birthMonth = nextMonth.monthValue,
        birthDay = nextMonth.dayOfMonth,
        birthYear = today.year - 27,
        relationship = "Friend",
        phoneNumber = "+919876543210",
        preferredLanguage = "Hindi",
        notes = "Planning surprise birthday video call 🎁",
        lastWishedYear = null
      )
    )

    birthdayDao.insertAll(samples.map { BirthdayEntity.fromModel(it) })
  }

  suspend fun seedInitialBirthdaysIfEmpty(userId: String, existingCount: Int) {
    if (existingCount > 0) return
    seedSampleBirthdays(userId)
  }
}
