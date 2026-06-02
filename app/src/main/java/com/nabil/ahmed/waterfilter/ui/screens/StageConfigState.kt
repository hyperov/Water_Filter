package com.nabil.ahmed.waterfilter.ui.screens

import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.data.util.StageNumber

data class StageConfigState(
    val stageNumber: StageNumber,
    val nameResId: Int,
    val descriptionResId: Int,
    val lifespan: Lifespan,
    val lastChangedTimestamp: Long? = System.currentTimeMillis(),
    val selection: SelectionType = SelectionType.TODAY
)

enum class SelectionType {
    TODAY, SPECIFIC_DATE, DONT_REMEMBER
}
