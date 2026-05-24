package com.example.classroomassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val content: String,
    val type: String,
    val important: Boolean
)
