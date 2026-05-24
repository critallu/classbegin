package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.classroomassistant.data.entity.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM AppSettings WHERE id = 1")
    fun observeSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: AppSettings)

    @Query("DELETE FROM Course")
    suspend fun clearCourses()

    @Query("DELETE FROM ReminderRule")
    suspend fun clearReminders()

    @Query("DELETE FROM CalendarEvent")
    suspend fun clearEvents()
}
