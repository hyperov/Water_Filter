package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_logs")
data class MaintenanceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filterId: Long,
    val stageNumber: Int,
    val stageName: String,
    val changeDateTimestamp: Long
)
