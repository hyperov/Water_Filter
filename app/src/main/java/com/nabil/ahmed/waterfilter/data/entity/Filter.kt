package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nabil.ahmed.waterfilter.data.util.FilterType

@Entity(tableName = "filters")
data class Filter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: FilterType
)
