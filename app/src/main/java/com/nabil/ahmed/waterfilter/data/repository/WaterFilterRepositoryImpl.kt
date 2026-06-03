package com.nabil.ahmed.waterfilter.data.repository

import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import com.nabil.ahmed.waterfilter.data.entity.*
import com.nabil.ahmed.waterfilter.data.util.FilterType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterFilterRepositoryImpl @Inject constructor(
    private val dao: FilterDao
) : WaterFilterRepository {

    override fun getAllFiltersWithCandles(): Flow<List<FilterWithCandles>> {
        return dao.getAllFiltersWithCandles()
    }

    override fun getAllMaintenanceLogs(): Flow<List<MaintenanceLog>> {
        return dao.getAllMaintenanceLogs()
    }

    override suspend fun saveFilter(name: String, type: FilterType, candles: List<Candle>) {
        val filterId = dao.insertFilter(Filter(name = name, type = type))
        val candlesWithId = candles.map { it.copy(filterId = filterId) }
        dao.insertCandles(candlesWithId)
    }

    override suspend fun updateCandle(candle: Candle) {
        val updatedCandle = candle.copy(lastChangedTimestamp = System.currentTimeMillis())
        dao.updateCandle(updatedCandle)
        
        val log = MaintenanceLog(
            filterId = candle.filterId,
            candleNumber = candle.candleNumber.value,
            candleName = candle.name,
            changeDateTimestamp = updatedCandle.lastChangedTimestamp
        )
        dao.insertMaintenanceLog(log)
    }

    override suspend fun deleteMaintenanceLog(log: MaintenanceLog) {
        dao.deleteMaintenanceLog(log)
    }

    override suspend fun deleteFilter(filter: Filter) {
        dao.deleteFilter(filter)
    }
}
