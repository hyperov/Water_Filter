package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import com.nabil.ahmed.waterfilter.R

@Composable
fun MainLayout(
    backStack: NavBackStack<Route>,
    currentRoute: Route,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Route.Dashboard,
                    onClick = {
                        if (currentRoute != Route.Dashboard) {
                            backStack.removeLastOrNull()
                            backStack.add(Route.Dashboard)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.dashboard)) }
                )
                NavigationBarItem(
                    selected = currentRoute == Route.MaintenanceHistory,
                    onClick = {
                        if (currentRoute != Route.MaintenanceHistory) {
                            backStack.removeLastOrNull()
                            backStack.add(Route.MaintenanceHistory)
                        }
                    },
                    icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                    label = { Text(stringResource(R.string.history)) }
                )
                NavigationBarItem(
                    selected = currentRoute == Route.Settings,
                    onClick = {
                        if (currentRoute != Route.Settings) {
                            backStack.removeLastOrNull()
                            backStack.add(Route.Settings)
                        }
                    },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings)) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
