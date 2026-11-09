package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.ui.components.AdBanner
import com.nabil.ahmed.waterfilter.ui.screens.CandlesConfigurationScreen
import com.nabil.ahmed.waterfilter.ui.screens.DashboardScreen
import com.nabil.ahmed.waterfilter.ui.screens.FilterRegistrationScreen
import com.nabil.ahmed.waterfilter.ui.screens.HistoryScreen
import com.nabil.ahmed.waterfilter.ui.screens.NotificationsScreen
import com.nabil.ahmed.waterfilter.ui.screens.OnboardingScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.ui.viewmodel.NotificationsViewModel
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel

@Composable
fun AppNavigation(
    backStack: NavBackStack<Route>
) {
    // The stateful version of AppNavigation provides the ViewModels using hiltViewModel()
    // and passes down the necessary callbacks and screen content to the stateless version.
    val viewModel: WaterFilterViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()

    AppNavigationContent(
        backStack = backStack,
        onSetOnboardingCompleted = { notificationsViewModel.setOnboardingCompleted(it) },
        onSaveFilter = { name, type, candles -> viewModel.saveFilter(name, type, candles) },
        dashboardScreen = {
            DashboardScreen(
                viewModel = viewModel,
                onAddFilter = { backStack.add(Route.FilterRegistration) }
            )
        },
        historyScreen = {
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
    )
}

/**
 * A stateless version of AppNavigation that can be previewed without Hilt or ViewModels.
 */
@Composable
fun AppNavigationContent(
    backStack: NavBackStack<Route>,
    onSetOnboardingCompleted: (Boolean) -> Unit,
    onSaveFilter: (String, FilterType, List<Candle>) -> Unit,
    dashboardScreen: @Composable () -> Unit,
    historyScreen: @Composable () -> Unit
) {
    val context = LocalContext.current

    val myEntryProvider = remember(backStack) {
        entryProvider {
            entry<Route.Onboarding> {
                OnboardingScreen(onStartClick = {
                    onSetOnboardingCompleted(true)
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
                        onSaveFilter(route.filterName, route.filterType, candles)
                        
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
                dashboardScreen()
            }
            entry<Route.MaintenanceHistory> {
                historyScreen()
            }
            entry<Route.Notifications> {
                NotificationsScreen()
            }
        }
    }

    val currentRoute = backStack.last()
    val showBottomBar = currentRoute is Route.Dashboard || 
                       currentRoute is Route.MaintenanceHistory || 
                       currentRoute is Route.Notifications

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        val navBarItemColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        NavigationBarItem(
                            colors = navBarItemColors,
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
                            colors = navBarItemColors,
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
                            colors = navBarItemColors,
                            selected = currentRoute is Route.Notifications,
                            onClick = {
                                if (currentRoute !is Route.Notifications) {
                                    backStack.add(Route.Notifications)
                                }
                            },
                            icon = { Icon(Icons.Rounded.Notifications, contentDescription = null) },
                            label = { Text("الإشعارات") }
                        )
                        NavigationBarItem(
                            colors = navBarItemColors,
                            selected = false,
                            onClick = {
                                if (currentRoute !is Route.Notifications) {
                                    backStack.add(Route.Notifications)
                                }
                            },
                            icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                            label = { Text(stringResource(R.string.settings)) }
                        )
                    }
                }
                AdBanner()
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
        // For preview purposes, we use the stateless AppNavigationContent
        // and provide mock implementations for the ViewModels' dependencies.
        AppNavigationContent(
            backStack = NavBackStack(Route.Dashboard),
            onSetOnboardingCompleted = {},
            onSaveFilter = { _, _, _ -> },
            dashboardScreen = {
                // We can use a simple placeholder or the Content version of the screen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Dashboard Preview")
                }
            },
            historyScreen = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("History Preview")
                }
            }
        )
    }
}
