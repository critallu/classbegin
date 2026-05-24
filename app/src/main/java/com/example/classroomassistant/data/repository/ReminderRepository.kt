package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.ReminderDao
import com.example.classroomassistant.data.entity.ReminderRule

class ReminderRepository(private val dao: ReminderDao) {
    fun observeByCourse(courseId: Long) = dao.observeByCourse(courseId)
    suspend fun add(rule: ReminderRule) = dao.insert(rule)
    suspend fun addAll(rules: List<ReminderRule>) = dao.insertAll(rules)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
    suspend fun deleteByCourseId(courseId: Long) = dao.deleteByCourseId(courseId)
}
