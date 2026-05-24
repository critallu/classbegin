package com.example.classroomassistant

import android.app.Application
import android.content.Context
import com.example.classroomassistant.data.AppDatabase
import com.example.classroomassistant.data.repository.CalendarRepository
import com.example.classroomassistant.data.repository.CourseRepository
import com.example.classroomassistant.data.repository.CourseWeekOverrideRepository
import com.example.classroomassistant.data.repository.ReminderRepository
import com.example.classroomassistant.data.repository.SemesterRepository
import com.example.classroomassistant.data.repository.SettingsRepository

class ClassroomAssistantApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

class AppContainer(application: Application) {
    val appContext: Context = application.applicationContext
    private val db = AppDatabase.getInstance(application)
    val courseRepository = CourseRepository(db.courseDao())
    val reminderRepository = ReminderRepository(db.reminderDao())
    val calendarRepository = CalendarRepository(db.calendarEventDao())
    val settingsRepository = SettingsRepository(db.settingsDao())
    val semesterRepository = SemesterRepository(db.semesterDao())
    val courseWeekOverrideRepository = CourseWeekOverrideRepository(db.courseWeekOverrideDao())
}
