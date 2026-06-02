package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.Lifespan

@Entity(
    tableName = "candles",
    foreignKeys = [
        ForeignKey(
            entity = Filter::class,
            parentColumns = ["id"],
            childColumns = ["filterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["filterId"])]
)
data class Candle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filterId: Long,
    val candleNumber: CandleNumber,
    val name: String,
    val lifespanDays: Lifespan,
    val lastChangedTimestamp: Long
)
