package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.SettingsDao
import com.example.classroomassistant.data.entity.AppSettings

class SettingsRepository(private val dao: SettingsDao) {
    val settings = dao.observeSettings()
    suspend fun upsert(settings: AppSettings) = dao.upsert(settings)
    suspend fun clearAllData() {
        dao.clearReminders()
        dao.clearCourses()
        dao.clearEvents()
    }
}
