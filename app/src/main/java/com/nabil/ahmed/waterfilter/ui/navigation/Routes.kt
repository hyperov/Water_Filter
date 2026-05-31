package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Route : NavKey {
    @Serializable
    data object Onboarding : Route()

    @Serializable
    data object FilterRegistration : Route()

    @Serializable
    data class StageConfiguration(val filterName: String, val filterType: Int) : Route()

    @Serializable
    data object Dashboard : Route()

    @Serializable
    data object MaintenanceHistory : Route()

    @Serializable
    data object Settings : Route()
}
