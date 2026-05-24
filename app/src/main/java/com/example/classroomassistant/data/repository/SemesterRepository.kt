package com.example.classroomassistant.data.repository

import com.example.classroomassistant.data.dao.SemesterDao
import com.example.classroomassistant.data.entity.SemesterConfig

class SemesterRepository(private val dao: SemesterDao) {
    val config = dao.observeConfig()
    suspend fun upsert(config: SemesterConfig) = dao.upsert(config)
}
