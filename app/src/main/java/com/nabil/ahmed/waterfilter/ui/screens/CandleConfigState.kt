package com.nabil.ahmed.waterfilter.ui.screens

import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.Lifespan

data class CandleConfigState(
    val candleNumber: CandleNumber,
    val nameResId: Int,
    val descriptionResId: Int,
    val lifespan: Lifespan,
    val lastChangedTimestamp: Long? = System.currentTimeMillis(),
    val selection: SelectionType = SelectionType.TODAY
)

enum class SelectionType {
    TODAY, SPECIFIC_DATE, DONT_REMEMBER
}
