package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.ui.screens.CandleConfigurationScreen
import com.nabil.ahmed.waterfilter.ui.screens.DashboardScreen
import com.nabil.ahmed.waterfilter.ui.screens.HistoryScreen
import com.nabil.ahmed.waterfilter.ui.screens.OnboardingScreen
import com.nabil.ahmed.waterfilter.ui.screens.RegistrationScreen
import com.nabil.ahmed.waterfilter.ui.screens.SettingsScreen
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel

@Composable
fun AppNavigation(
    backStack: NavBackStack<Route>
) {
    val viewModel: WaterFilterViewModel = hiltViewModel()

    val myEntryProvider = remember {
        entryProvider {
            entry<Route.Onboarding> {
                OnboardingScreen(onStartClick = {
                    backStack.add(Route.FilterRegistration)
                })
            }
            entry<Route.FilterRegistration> {
                RegistrationScreen(
                    onNext = { name, type ->
                        backStack.add(Route.CandleConfiguration(name, type))
                    },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Route.CandleConfiguration> { route ->
                CandleConfigurationScreen(
                    filterName = route.filterName,
                    filterType = route.filterType,
                    onBack = { backStack.removeLastOrNull() },
                    onSave = { candlesConfig ->
                        val candles = candlesConfig.map { config ->
                            Candle(
                                filterId = 0L,
                                candleNumber = config.candleNumber,
                                name = "res", // placeholder, we might need a way to get the actual name
                                lifespanDays = config.lifespan,
                                lastChangedTimestamp = config.lastChangedTimestamp ?: System.currentTimeMillis()
                            )
                        }
                        // We need a way to actually save these, maybe via viewModel
                        // viewModel.saveFilter(route.filterName, route.filterType, candles)
                        backStack.add(Route.Dashboard)
                    }
                )
            }
            entry<Route.Dashboard> {
                DashboardScreen(
                    viewModel = viewModel,
                    onAddFilter = { backStack.add(Route.FilterRegistration) }
                )
            }
            entry<Route.MaintenanceHistory> {
                HistoryScreen(viewModel = viewModel)
            }
            entry<Route.Settings> {
                SettingsScreen()
            }
        }
    }

    androidx.navigation3.ui.NavDisplay(
        backStack = backStack,
        entryProvider = myEntryProvider
    )
}
