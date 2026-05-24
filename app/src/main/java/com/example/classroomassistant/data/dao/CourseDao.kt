package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.classroomassistant.data.entity.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM Course ORDER BY weekday, startTime")
    fun observeCourses(): Flow<List<Course>>

    @Query("SELECT * FROM Course WHERE id = :id")
    fun observeCourse(id: Long): Flow<Course?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course): Long

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)
}
