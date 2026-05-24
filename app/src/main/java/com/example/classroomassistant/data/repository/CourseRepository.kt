package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.CourseDao
import com.example.classroomassistant.data.entity.Course

class CourseRepository(private val dao: CourseDao) {
    val courses = dao.observeCourses()
    fun observeCourse(id: Long) = dao.observeCourse(id)
    suspend fun add(course: Course) = dao.insert(course)
    suspend fun update(course: Course) = dao.update(course)
    suspend fun delete(course: Course) = dao.delete(course)
}
