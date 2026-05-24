package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.CourseWeekOverrideDao
import com.example.classroomassistant.data.entity.CourseWeekOverride

class CourseWeekOverrideRepository(private val dao: CourseWeekOverrideDao) {
    fun observeByWeek(weekIndex: Int) = dao.observeByWeek(weekIndex)
    suspend fun findByBaseAndWeek(baseCourseId: Long, weekIndex: Int) = dao.findByBaseAndWeek(baseCourseId, weekIndex)
    suspend fun add(override: CourseWeekOverride) = dao.insert(override)
    suspend fun update(override: CourseWeekOverride) = dao.update(override)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
