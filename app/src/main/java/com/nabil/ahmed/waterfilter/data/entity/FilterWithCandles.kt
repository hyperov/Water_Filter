package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FilterWithCandles(
    @Embedded val filter: Filter,
    @Relation(
        parentColumn = "id",
        entityColumn = "filterId"
    )
    val candles: List<Candle>
)
