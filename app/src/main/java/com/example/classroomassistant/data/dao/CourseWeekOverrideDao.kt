package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.classroomassistant.data.entity.CourseWeekOverride
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseWeekOverrideDao {
    @Query("SELECT * FROM CourseWeekOverride WHERE weekIndex = :weekIndex")
    fun observeByWeek(weekIndex: Int): Flow<List<CourseWeekOverride>>

    @Query("SELECT * FROM CourseWeekOverride WHERE baseCourseId = :baseCourseId AND weekIndex = :weekIndex LIMIT 1")
    suspend fun findByBaseAndWeek(baseCourseId: Long, weekIndex: Int): CourseWeekOverride?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(override: CourseWeekOverride): Long

    @Update
    suspend fun update(override: CourseWeekOverride)

    @Query("DELETE FROM CourseWeekOverride WHERE id = :id")
    suspend fun deleteById(id: Long)
}
