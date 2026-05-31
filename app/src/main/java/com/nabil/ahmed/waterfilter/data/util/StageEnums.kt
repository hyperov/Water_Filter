package com.nabil.ahmed.waterfilter.data.util

import com.nabil.ahmed.waterfilter.R

enum class StageNumber(val value: Int, val nameResId: Int, val descResId: Int) {
    STAGE_1(1, R.string.stage_pp, R.string.stage_pp_desc),
    STAGE_2(2, R.string.stage_gac, R.string.stage_gac_desc),
    STAGE_3(3, R.string.stage_cto, R.string.stage_cto_desc),
    STAGE_4(4, R.string.stage_ro, R.string.stage_ro_desc),
    STAGE_5(5, R.string.stage_post_carbon, R.string.stage_post_carbon_desc),
    STAGE_6(6, R.string.stage_mineral, R.string.stage_mineral_desc),
    STAGE_7(7, R.string.stage_alkaline, R.string.stage_alkaline_desc);

    companion object {
        fun fromInt(value: Int): StageNumber = values().find { it.value == value } ?: STAGE_1
    }
}

enum class Lifespan(val days: Int, val resId: Int) {
    MONTHS_3(90, R.string.change_every_3_months),
    MONTHS_6(180, R.string.change_every_6_months),
    MONTHS_12(365, R.string.change_every_12_months),
    MONTHS_24(730, R.string.change_every_24_months);

    companion object {
        fun fromDays(days: Int): Lifespan = values().find { it.days == days } ?: MONTHS_3
    }
}

enum class FilterType(val stagesCount: Int, val resId: Int) {
    STAGES_3(3, R.string.stages_3),
    STAGES_5(5, R.string.stages_5),
    STAGES_7(7, R.string.stages_7);

    companion object {
        fun fromInt(value: Int): FilterType = entries.find { it.stagesCount == value } ?: STAGES_3
    }
}
