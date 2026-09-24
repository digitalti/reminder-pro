package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.BirthdayItem

@Entity(
  tableName = "birthdays",
  indices = [Index(value = ["userId"]), Index(value = ["userId", "birthMonth", "birthDay"])]
)
data class BirthdayEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val name: String,
  val birthMonth: Int,
  val birthDay: Int,
  val birthYear: Int?,
  val relationship: String,
  val phoneNumber: String?,
  val preferredLanguage: String = "English",
  val notes: String,
  val lastWishedYear: Int?,
  val createdAt: Long,
  val updatedAt: Long
) {
  fun toModel(): BirthdayItem {
    return BirthdayItem(
      id = id,
      userId = userId,
      name = name,
      birthMonth = birthMonth,
      birthDay = birthDay,
      birthYear = birthYear,
      relationship = relationship,
      phoneNumber = phoneNumber,
      preferredLanguage = preferredLanguage,
      notes = notes,
      lastWishedYear = lastWishedYear,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
  }

  companion object {
    fun fromModel(model: BirthdayItem): BirthdayEntity {
      return BirthdayEntity(
        id = model.id,
        userId = model.userId,
        name = model.name,
        birthMonth = model.birthMonth,
        birthDay = model.birthDay,
        birthYear = model.birthYear,
        relationship = model.relationship,
        phoneNumber = model.phoneNumber,
        preferredLanguage = model.preferredLanguage,
        notes = model.notes,
        lastWishedYear = model.lastWishedYear,
        createdAt = model.createdAt,
        updatedAt = model.updatedAt
      )
    }
  }
}
