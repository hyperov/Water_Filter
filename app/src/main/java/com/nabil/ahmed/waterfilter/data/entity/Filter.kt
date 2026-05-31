package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class Filter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: Int // 3, 5, or 7 stages
)
