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
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class ReminderRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val minutesBeforeEnd: Int,
    val label: String,
    val enabled: Boolean = true
)
