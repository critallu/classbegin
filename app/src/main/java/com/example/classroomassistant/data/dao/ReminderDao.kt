package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.classroomassistant.data.entity.ReminderRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM ReminderRule WHERE courseId = :courseId ORDER BY triggerAfterMinutes ASC")
    fun observeByCourse(courseId: Long): Flow<List<ReminderRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: ReminderRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<ReminderRule>)

    @Query("DELETE FROM ReminderRule WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ReminderRule WHERE courseId = :courseId")
    suspend fun deleteByCourseId(courseId: Long)
}
