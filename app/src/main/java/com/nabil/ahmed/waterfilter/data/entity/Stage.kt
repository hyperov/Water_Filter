package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.data.util.StageNumber

@Entity(
    tableName = "stages",
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
data class Stage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filterId: Long,
    val stageNumber: StageNumber,
    val name: String,
    val lifespanDays: Lifespan,
    val lastChangedTimestamp: Long
)
