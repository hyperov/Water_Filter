package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nabil.ahmed.waterfilter.data.entity.Stage
import com.nabil.ahmed.waterfilter.ui.screens.*
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
                    onNext = { name, stages ->
                        backStack.add(Route.StageConfiguration(name, stages))
                    },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Route.StageConfiguration> { route ->
                val res = LocalResources.current
                StageConfigurationScreen(
                    filterName = route.filterName,
                    filterType = route.filterType,
                    onBack = { backStack.removeLastOrNull() },
                    onSave = { stagesConfig ->
                        val stages = stagesConfig.map { config ->
                            Stage(
                                filterId = 0, // Will be set in ViewModel
                                stageNumber = config.stageNumber,
                                name = res.getString(config.nameResId),
                                lifespanDays = config.lifespanDays,
                                lastChangedTimestamp = config.lastChangedTimestamp ?: 0L
                            )
                        }
                        viewModel.saveFilter(route.filterName, route.filterType, stages)
                        
                        while(backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(Route.Dashboard)
                    }
                )
            }
            entry<Route.Dashboard> {
                MainLayout(backStack = backStack, currentRoute = Route.Dashboard) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onAddFilter = { backStack.add(Route.FilterRegistration) }
                    )
                }
            }
            entry<Route.MaintenanceHistory> {
                MainLayout(backStack = backStack, currentRoute = Route.MaintenanceHistory) {
                    HistoryScreen(viewModel = viewModel)
                }
            }
            entry<Route.Settings> {
                MainLayout(backStack = backStack, currentRoute = Route.Settings) {
                    SettingsScreen()
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = myEntryProvider
    )
}
