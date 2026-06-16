package com.nabil.ahmed.waterfilter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.nabil.ahmed.waterfilter.ui.navigation.AppNavigation
import com.nabil.ahmed.waterfilter.ui.navigation.Route
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaterFilterTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    val settings by settingsViewModel.settings.collectAsState()
                    
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
