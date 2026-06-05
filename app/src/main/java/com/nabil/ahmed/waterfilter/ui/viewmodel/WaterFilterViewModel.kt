package com.nabil.ahmed.waterfilter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabil.ahmed.waterfilter.data.entity.*
import com.nabil.ahmed.waterfilter.data.repository.WaterFilterRepository
import com.nabil.ahmed.waterfilter.data.util.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterFilterViewModel @Inject constructor(
    private val repository: WaterFilterRepository
) : ViewModel() {

    val allFiltersWithCandles: StateFlow<List<FilterWithCandles>> = repository.getAllFiltersWithCandles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaintenanceLogs: StateFlow<List<MaintenanceLog>> = repository.getAllMaintenanceLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFilterId = MutableStateFlow<Long?>(null)
    val selectedFilterId: StateFlow<Long?> = _selectedFilterId.asStateFlow()

    fun saveFilter(name: String, type: FilterType, candles: List<Candle>) {
        viewModelScope.launch {
            val id = repository.saveFilter(name, type, candles)
            _selectedFilterId.value = id
        }
    }

    fun selectFilter(id: Long) {
        _selectedFilterId.value = id
    }

    fun updateCandle(candle: Candle, customTimestamp: Long? = null) {
        viewModelScope.launch {
            val updatedCandle = candle.copy(lastChangedTimestamp = customTimestamp ?: System.currentTimeMillis())
            repository.updateCandle(updatedCandle)
        }
    }

    fun deleteMaintenanceLog(log: MaintenanceLog) {
        viewModelScope.launch {
            repository.deleteMaintenanceLog(log)
        }
    }

    fun deleteFilter(filter: Filter) {
        viewModelScope.launch {
            repository.deleteFilter(filter)
            if (_selectedFilterId.value == filter.id) {
                _selectedFilterId.value = null
            }
        }
    }
}
