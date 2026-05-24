package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.classroomassistant.data.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM CalendarEvent ORDER BY date, time")
    fun observeEvents(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM CalendarEvent WHERE date = :date ORDER BY time")
    fun observeByDate(date: String): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEvent): Long

    @Update
    suspend fun update(event: CalendarEvent)

    @Delete
    suspend fun delete(event: CalendarEvent)
}
