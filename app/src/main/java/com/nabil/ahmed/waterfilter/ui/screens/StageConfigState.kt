package com.nabil.ahmed.waterfilter.ui.screens

data class StageConfigState(
    val stageNumber: Int,
    val nameResId: Int,
    val lifespanDays: Int,
    val lastChangedTimestamp: Long? = null,
    val selection: SelectionType = SelectionType.DONT_REMEMBER
)

enum class SelectionType {
    TODAY, SPECIFIC_DATE, DONT_REMEMBER
}
