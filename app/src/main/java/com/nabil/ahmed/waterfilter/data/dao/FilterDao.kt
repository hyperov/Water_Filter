package com.nabil.ahmed.waterfilter.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithCandles
import com.nabil.ahmed.waterfilter.data.entity.MaintenanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Transaction
    @Query("SELECT * FROM filters")
    fun getAllFiltersWithCandles(): Flow<List<FilterWithCandles>>

    @Transaction
    @Query("SELECT * FROM filters")
    suspend fun getAllFiltersWithCandlesSync(): List<FilterWithCandles>

    @Insert
    suspend fun insertFilter(filter: Filter): Long

    @Insert
    suspend fun insertCandles(candles: List<Candle>)

    @Insert
    suspend fun insertCandle(candle: Candle): Long

    @Update
    suspend fun updateCandle(candle: Candle)

    @Insert
    suspend fun insertMaintenanceLog(log: MaintenanceLog)

    @Query("SELECT * FROM maintenance_logs ORDER BY changeDateTimestamp DESC")
    fun getAllMaintenanceLogs(): Flow<List<MaintenanceLog>>

    @Delete
    suspend fun deleteMaintenanceLog(log: MaintenanceLog)

    @Delete
    suspend fun deleteFilter(filter: Filter)

    @Delete
    suspend fun deleteCandle(candle: Candle)

    @Query("SELECT * FROM candles WHERE filterId = :filterId")
    suspend fun getCandlesByFilterId(filterId: Long): List<Candle>
}
