package com.nabil.ahmed.waterfilter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import com.nabil.ahmed.waterfilter.data.entity.*
import com.nabil.ahmed.waterfilter.data.util.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterFilterViewModel @Inject constructor(
    private val dao: FilterDao
) : ViewModel() {

    val allFiltersWithCandles: StateFlow<List<FilterWithCandles>> = dao.getAllFiltersWithCandles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaintenanceLogs: StateFlow<List<MaintenanceLog>> = dao.getAllMaintenanceLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveFilter(name: String, type: FilterType, candles: List<Candle>) {
        viewModelScope.launch {
            val filterId = dao.insertFilter(Filter(name = name, type = type))
            val candlesWithId = candles.map { it.copy(filterId = filterId) }
            dao.insertCandles(candlesWithId)
        }
    }

    fun updateCandle(candle: Candle) {
        viewModelScope.launch {
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
    }

    fun deleteMaintenanceLog(log: MaintenanceLog) {
        viewModelScope.launch {
            dao.deleteMaintenanceLog(log)
        }
    }

    fun deleteFilter(filter: Filter) {
        viewModelScope.launch {
            dao.deleteFilter(filter)
        }
    }
}
