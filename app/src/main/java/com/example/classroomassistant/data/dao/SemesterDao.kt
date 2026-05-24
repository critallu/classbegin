package com.example.classroomassistant.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.classroomassistant.data.entity.SemesterConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Query("SELECT * FROM SemesterConfig WHERE id = 1")
    fun observeConfig(): Flow<SemesterConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: SemesterConfig)

    @Update
    suspend fun update(config: SemesterConfig)
}
