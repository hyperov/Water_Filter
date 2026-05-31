package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var enableAll by remember { mutableStateOf(true) }
    var earlyWarnings by remember { mutableStateOf(true) }
    var dueDateReminders by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }
    var quietHours by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification_settings),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.settings_img),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Save logic
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.save_changes),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Enable Switch
            SettingsCard {
                SettingsToggleItem(
                    title = stringResource(R.string.enable_all_notifications),
                    icon = Icons.Rounded.Notifications,
                    checked = enableAll,
                    onCheckedChange = { enableAll = it },
                    isMain = true
                )
            }

            // Filter Reminders Section
            SectionHeader(title = stringResource(R.string.filter_reminders))
            SettingsCard {
                Column {
                    SettingsToggleItem(
                        title = stringResource(R.string.early_warnings),
                        checked = earlyWarnings,
                        onCheckedChange = { earlyWarnings = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    SettingsToggleItem(
                        title = stringResource(R.string.due_date_reminders),
                        checked = dueDateReminders,
                        onCheckedChange = { dueDateReminders = it }
                    )
                }
            }

            // Preferences Section
            SectionHeader(title = stringResource(R.string.preferences))
            SettingsCard {
                Column {
                    SettingsToggleItem(
                        title = stringResource(R.string.sound),
                        icon = Icons.Rounded.VolumeUp,
                        checked = sound,
                        onCheckedChange = { sound = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    SettingsToggleItem(
                        title = stringResource(R.string.vibration),
                        icon = Icons.Rounded.Vibration,
                        checked = vibration,
                        onCheckedChange = { vibration = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    SettingsToggleItem(
                        title = stringResource(R.string.quiet_hours),
                        icon = Icons.Rounded.DoNotDisturbOn,
                        checked = quietHours,
                        onCheckedChange = { quietHours = it }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    WaterFilterTheme {
        SettingsScreen()
    }
}
