package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.CalendarEventDao
import com.example.classroomassistant.data.entity.CalendarEvent

class CalendarRepository(private val dao: CalendarEventDao) {
    val events = dao.observeEvents()
    fun observeByDate(date: String) = dao.observeByDate(date)
    suspend fun add(event: CalendarEvent) = dao.insert(event)
    suspend fun update(event: CalendarEvent) = dao.update(event)
    suspend fun delete(event: CalendarEvent) = dao.delete(event)
}
