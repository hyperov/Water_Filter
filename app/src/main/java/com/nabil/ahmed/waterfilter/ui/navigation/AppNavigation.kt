package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.ui.screens.CandlesConfigurationScreen
import com.nabil.ahmed.waterfilter.ui.screens.DashboardScreen
import com.nabil.ahmed.waterfilter.ui.screens.FilterRegistrationScreen
import com.nabil.ahmed.waterfilter.ui.screens.HistoryScreen
import com.nabil.ahmed.waterfilter.ui.screens.OnboardingScreen
import com.nabil.ahmed.waterfilter.ui.screens.SettingsScreen
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel
import com.nabil.ahmed.waterfilter.ui.viewmodel.SettingsViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.data.util.FilterType

@Composable
fun AppNavigation(
    backStack: NavBackStack<Route>
) {
    val viewModel: WaterFilterViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current

    val myEntryProvider = remember(backStack) {
        entryProvider {
            entry<Route.Onboarding> {
                OnboardingScreen(onStartClick = {
                    settingsViewModel.setOnboardingCompleted(true)
                    backStack.add(Route.FilterRegistration)
                })
            }
            entry<Route.FilterRegistration> {
                FilterRegistrationScreen(
                    onNext = { name, type ->
                        backStack.add(Route.CandleConfiguration(name, type))
                    },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Route.CandleConfiguration> { route ->
                CandlesConfigurationScreen(
                    filterName = route.filterName,
                    filterType = route.filterType,
                    onBack = { backStack.removeLastOrNull() },
                    onSave = { candlesConfig ->
                        val candles = candlesConfig.map { config ->
                            Candle(
                                filterId = 0L,
                                candleNumber = config.candleNumber,
                                name = context.getString(config.nameResId),
                                lifespanDays = config.lifespan,
                                lastChangedTimestamp = config.lastChangedTimestamp ?: System.currentTimeMillis()
                            )
                        }
                        viewModel.saveFilter(route.filterName, route.filterType, candles)
                        
                        // Clear the registration flow and onboarding from backstack and go to Dashboard
                        while(backStack.isNotEmpty() && (
                            backStack.last() is Route.FilterRegistration || 
                            backStack.last() is Route.CandleConfiguration ||
                            backStack.last() is Route.Onboarding
                        )) {
                            backStack.removeLastOrNull()
                        }
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
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToDashboard = {
                        while (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                        if (backStack.last() !is Route.Dashboard) {
                            backStack.add(Route.Dashboard)
                        }
                    }
                )
            }
            entry<Route.Settings> {
                SettingsScreen()
            }
        }
    }

    val currentRoute = backStack.last()
    val showBottomBar = currentRoute is Route.Dashboard || 
                       currentRoute is Route.MaintenanceHistory || 
                       currentRoute is Route.Settings

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute is Route.Dashboard,
                        onClick = {
                            if (currentRoute !is Route.Dashboard) {
                                while (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                                if (backStack.last() !is Route.Dashboard) {
                                    backStack.add(Route.Dashboard)
                                }
                            }
                        },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.dashboard)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute is Route.MaintenanceHistory,
                        onClick = {
                            if (currentRoute !is Route.MaintenanceHistory) {
                                backStack.add(Route.MaintenanceHistory)
                            }
                        },
                        icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                        label = { Text(stringResource(R.string.history)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute is Route.Settings,
                        onClick = {
                            if (currentRoute !is Route.Settings) {
                                backStack.add(Route.Settings)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.settings)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            NavDisplay(
                backStack = backStack,
                entryProvider = myEntryProvider
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    WaterFilterTheme {
        // For preview purposes, we use a simple mock backstack
        AppNavigation(backStack = NavBackStack(Route.Dashboard))
    }
}
