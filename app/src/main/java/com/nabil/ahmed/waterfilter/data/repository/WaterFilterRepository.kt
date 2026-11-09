package com.nabil.ahmed.waterfilter.data.repository

import com.nabil.ahmed.waterfilter.data.entity.*
import com.nabil.ahmed.waterfilter.data.util.FilterType
import kotlinx.coroutines.flow.Flow

interface WaterFilterRepository {
    fun getAllFiltersWithCandles(): Flow<List<FilterWithCandles>>
    fun getAllMaintenanceLogs(): Flow<List<MaintenanceLog>>
    suspend fun saveFilter(name: String, type: FilterType, candles: List<Candle>): Long
    suspend fun addCandleToFilter(candle: Candle): Long
    suspend fun updateCandle(candle: Candle, logMaintenance: Boolean = true)
    suspend fun deleteMaintenanceLog(log: MaintenanceLog)
    suspend fun deleteFilter(filter: Filter)
    suspend fun deleteCandle(candle: Candle)
}
