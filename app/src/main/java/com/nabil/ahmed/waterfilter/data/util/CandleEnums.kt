package com.nabil.ahmed.waterfilter.data.util

import com.nabil.ahmed.waterfilter.R

enum class CandleNumber(val value: Int, val nameResId: Int, val descResId: Int) {
    CANDLE_1(1, R.string.candle_pp, R.string.candle_pp_desc),
    CANDLE_2(2, R.string.candle_gac, R.string.candle_gac_desc),
    CANDLE_3(3, R.string.candle_cto, R.string.candle_cto_desc),
    CANDLE_4(4, R.string.candle_ro, R.string.candle_ro_desc),
    CANDLE_5(5, R.string.candle_post_carbon, R.string.candle_post_carbon_desc),
    CANDLE_6(6, R.string.candle_mineral, R.string.candle_mineral_desc),
    CANDLE_7(7, R.string.candle_alkaline, R.string.candle_alkaline_desc);

    companion object {
        fun fromInt(value: Int): CandleNumber = entries.find { it.value == value } ?: CANDLE_1
    }
}

enum class Lifespan(val days: Int, val resId: Int) {
    MONTHS_3(90, R.string.change_every_3_months),
    MONTHS_6(180, R.string.change_every_6_months),
    MONTHS_12(365, R.string.change_every_12_months),
    MONTHS_24(730, R.string.change_every_24_months);

    companion object {
        fun fromDays(days: Int): Lifespan = entries.find { it.days == days } ?: MONTHS_3
    }
}

enum class FilterType(val candlesCount: Int, val resId: Int) {
    CANDLES_3(3, R.string.candles_3),
    CANDLES_5(5, R.string.candles_5),
    CANDLES_7(7, R.string.candles_7);

    companion object {
        fun fromInt(value: Int): FilterType = entries.find { it.candlesCount == value } ?: CANDLES_3
    }
}
