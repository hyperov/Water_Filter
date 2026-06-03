package com.nabil.ahmed.waterfilter.ui.screens

import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.Lifespan

data class CandleConfigState(
    val candleNumber: CandleNumber,
    val nameResId: Int,
    val descriptionResId: Int,
    val lifespan: Lifespan,
    val lastChangedTimestamp: Long? = null,
    val selection: SelectionType = SelectionType.NONE
)

enum class SelectionType {
    NONE, TODAY, SPECIFIC_DATE, DONT_REMEMBER
}
