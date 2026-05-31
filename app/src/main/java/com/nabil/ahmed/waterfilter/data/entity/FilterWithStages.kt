package com.nabil.ahmed.waterfilter.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FilterWithStages(
    @Embedded val filter: Filter,
    @Relation(
        parentColumn = "id",
        entityColumn = "filterId"
    )
    val stages: List<Stage>
)
