package com.nabil.ahmed.waterfilter.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.nabil.ahmed.waterfilter.R

@Composable
fun MainLayout(
    backStack: NavBackStack<Route>,
    content: @Composable () -> Unit
) {
    val currentRoute = backStack.last()
    val showBottomBar = currentRoute is Route.Dashboard || 
                       currentRoute is Route.MaintenanceHistory || 
                       currentRoute is Route.Notifications

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
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
                        selected = currentRoute is Route.Notifications,
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            content()
        }
    }
}
