package com.nabil.ahmed.waterfilter.data.util

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCandleNumber(value: CandleNumber): Int = value.value

    @TypeConverter
    fun toCandleNumber(value: Int): CandleNumber = CandleNumber.fromInt(value)

    @TypeConverter
    fun fromLifespan(value: Lifespan): Int = value.days

    @TypeConverter
    fun toLifespan(value: Int): Lifespan = Lifespan.fromDays(value)

    @TypeConverter
    fun fromFilterType(value: FilterType): Int = value.candlesCount

    @TypeConverter
    fun toFilterType(value: Int): FilterType = FilterType.fromInt(value)
}
