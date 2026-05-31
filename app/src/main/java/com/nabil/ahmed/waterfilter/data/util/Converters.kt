package com.nabil.ahmed.waterfilter.data.util

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStageNumber(value: StageNumber): Int = value.value

    @TypeConverter
    fun toStageNumber(value: Int): StageNumber = StageNumber.fromInt(value)

    @TypeConverter
    fun fromLifespan(value: Lifespan): Int = value.days

    @TypeConverter
    fun toLifespan(value: Int): Lifespan = Lifespan.fromDays(value)

    @TypeConverter
    fun fromFilterType(value: FilterType): Int = value.stagesCount

    @TypeConverter
    fun toFilterType(value: Int): FilterType = FilterType.fromInt(value)
}
