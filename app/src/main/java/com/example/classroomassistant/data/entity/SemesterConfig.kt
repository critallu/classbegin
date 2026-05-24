package com.example.classroomassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SemesterConfig(
    @PrimaryKey val id: Int = 1,
    val totalWeeks: Int = 20,
    val currentWeek: Int = 1,
    val termStartDate: String = "2026-09-01",
    val periodsPerDay: Int = 8,
    val periodDurationMinutes: Int = 45
)
