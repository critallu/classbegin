package com.example.classroomassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val teacherName: String = "Ada",
    val defaultDurationMinutes: Int = 40,
    val vibrationEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)
