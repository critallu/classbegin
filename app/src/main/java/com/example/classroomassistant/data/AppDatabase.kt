package com.example.classroomassistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.classroomassistant.data.dao.CalendarEventDao
import com.example.classroomassistant.data.dao.CourseDao
import com.example.classroomassistant.data.dao.ReminderDao
import com.example.classroomassistant.data.dao.SettingsDao
import com.example.classroomassistant.data.entity.AppSettings
import com.example.classroomassistant.data.entity.CalendarEvent
import com.example.classroomassistant.data.entity.Course
import com.example.classroomassistant.data.entity.ReminderRule

@Database(
    entities = [Course::class, ReminderRule::class, CalendarEvent::class, AppSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun reminderDao(): ReminderDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "classroom_assistant.db"
            ).build().also { INSTANCE = it }
        }
    }
}
