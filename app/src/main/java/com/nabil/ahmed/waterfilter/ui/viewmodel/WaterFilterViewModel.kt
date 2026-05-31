package com.nabil.ahmed.waterfilter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithStages
import com.nabil.ahmed.waterfilter.data.entity.MaintenanceLog
import com.nabil.ahmed.waterfilter.data.entity.Stage
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

    val allFiltersWithStages: StateFlow<List<FilterWithStages>> = dao.getAllFiltersWithStages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaintenanceLogs: StateFlow<List<MaintenanceLog>> = dao.getAllMaintenanceLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveFilter(name: String, type: FilterType, stages: List<Stage>) {
        viewModelScope.launch {
            val filterId = dao.insertFilter(Filter(name = name, type = type))
            val stagesWithId = stages.map { it.copy(filterId = filterId) }
            dao.insertStages(stagesWithId)
        }
    }

    fun updateStage(stage: Stage) {
        viewModelScope.launch {
            val updatedStage = stage.copy(lastChangedTimestamp = System.currentTimeMillis())
            dao.updateStage(updatedStage)
            
            val log = MaintenanceLog(
                filterId = stage.filterId,
                stageNumber = stage.stageNumber.value,
                stageName = stage.name,
                changeDateTimestamp = updatedStage.lastChangedTimestamp
            )
            dao.insertMaintenanceLog(log)
        }
    }

    fun deleteMaintenanceLog(log: MaintenanceLog) {
        viewModelScope.launch {
            dao.deleteMaintenanceLog(log)
        }
    }
}
