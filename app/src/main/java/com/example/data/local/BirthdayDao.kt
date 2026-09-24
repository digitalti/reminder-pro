package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {
  @Query("SELECT * FROM birthdays WHERE userId = :userId ORDER BY birthMonth ASC, birthDay ASC")
  fun getBirthdaysForUser(userId: String): Flow<List<BirthdayEntity>>

  @Query("SELECT * FROM birthdays WHERE userId = :userId AND id = :id LIMIT 1")
  suspend fun getBirthdayById(userId: String, id: String): BirthdayEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBirthday(birthday: BirthdayEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(birthdays: List<BirthdayEntity>)

  @Update
  suspend fun updateBirthday(birthday: BirthdayEntity)

  @Query("DELETE FROM birthdays WHERE id = :id AND userId = :userId")
  suspend fun deleteBirthday(id: String, userId: String)

  @Query("UPDATE birthdays SET lastWishedYear = :year, updatedAt = :updatedAt WHERE id = :id AND userId = :userId")
  suspend fun markAsWished(id: String, userId: String, year: Int, updatedAt: Long)

  @Query("DELETE FROM birthdays WHERE userId = :userId")
  suspend fun deleteAllForUser(userId: String)
}
