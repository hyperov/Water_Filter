package com.nabil.ahmed.waterfilter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.nabil.ahmed.waterfilter.ui.navigation.AppNavigation
import com.nabil.ahmed.waterfilter.ui.navigation.Route
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.ui.viewmodel.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterFilterTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
                    val settings by notificationsViewModel.settings.collectAsState()
                    
                    if (settings.isLoaded) {
                        val initialRoute = if (settings.onboardingCompleted) Route.Dashboard else Route.Onboarding
                        val backStack = rememberNavBackStack(initialRoute)

                        @Suppress("UNCHECKED_CAST")
                        AppNavigation(backStack = backStack as NavBackStack<Route>)
                    }
                }
            }
        }
    }
}
