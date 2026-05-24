package com.example.classroomassistant.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["baseCourseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("baseCourseId"), Index("weekIndex")]
)
data class CourseWeekOverride(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val baseCourseId: Long,
    val weekIndex: Int,
    val name: String,
    val weekday: Int,
    val startTime: String,
    val durationMinutes: Int,
    val classroom: String,
    val className: String,
    val note: String,
    val color: String
)
