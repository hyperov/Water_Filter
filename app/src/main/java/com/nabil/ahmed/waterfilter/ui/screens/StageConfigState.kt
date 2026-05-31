package com.nabil.ahmed.waterfilter.ui.screens

data class StageConfigState(
    val stageNumber: Int,
    val nameResId: Int,
    val lifespanDays: Int,
    var lastChangedTimestamp: Long? = null,
    var selection: SelectionType = SelectionType.DONT_REMEMBER
)

enum class SelectionType {
    TODAY, SPECIFIC_DATE, DONT_REMEMBER
}
