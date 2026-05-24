package com.example.classroomassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val weekday: Int,
    val startTime: String,
    val durationMinutes: Int,
    val classroom: String,
    val className: String,
    val note: String,
    val color: String
)
