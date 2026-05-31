package com.nabil.ahmed.waterfilter.data.dao

import androidx.room.*
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithStages
import com.nabil.ahmed.waterfilter.data.entity.MaintenanceLog
import com.nabil.ahmed.waterfilter.data.entity.Stage
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Transaction
    @Query("SELECT * FROM filters")
    fun getAllFiltersWithStages(): Flow<List<FilterWithStages>>

    @Transaction
    @Query("SELECT * FROM filters")
    suspend fun getAllFiltersWithStagesSync(): List<FilterWithStages>

    @Insert
    suspend fun insertFilter(filter: Filter): Long

    @Insert
    suspend fun insertStages(stages: List<Stage>)

    @Update
    suspend fun updateStage(stage: Stage)

    @Insert
    suspend fun insertMaintenanceLog(log: MaintenanceLog)

    @Query("SELECT * FROM maintenance_logs ORDER BY changeDateTimestamp DESC")
    fun getAllMaintenanceLogs(): Flow<List<MaintenanceLog>>

    @Delete
    suspend fun deleteMaintenanceLog(log: MaintenanceLog)

    @Delete
    suspend fun deleteFilter(filter: Filter)
}
