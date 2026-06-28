package com.nabil.ahmed.waterfilter.ui.screens

import android.Manifest
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.repository.UserSettings
import com.nabil.ahmed.waterfilter.ui.components.AdBanner
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.ui.viewmodel.NotificationsViewModel
import java.util.Locale

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    
    NotificationsContent(
        settings = settings,
        onToggleEnableAll = { viewModel.toggleEnableAll(it) },
        onToggleEarlyWarnings = { viewModel.toggleEarlyWarnings(it) },
        onToggleDueDateReminders = { viewModel.toggleDueDateReminders(it) },
        onToggleSound = { viewModel.toggleSound(it) },
        onToggleVibration = { viewModel.toggleVibration(it) },
        onToggleQuietHours = { viewModel.toggleQuietHours(it) },
        onUpdateQuietHoursStart = { viewModel.updateQuietHoursStart(it) },
        onUpdateQuietHoursEnd = { viewModel.updateQuietHoursEnd(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NotificationsContent(
    settings: UserSettings,
    onToggleEnableAll: (Boolean) -> Unit,
    onToggleEarlyWarnings: (Boolean) -> Unit,
    onToggleDueDateReminders: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onToggleQuietHours: (Boolean) -> Unit,
    onUpdateQuietHoursStart: (String) -> Unit,
    onUpdateQuietHoursEnd: (String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    if (showStartTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = settings.quietHoursStart.split(":")[0].toInt(),
            initialMinute = settings.quietHoursStart.split(":")[1].toInt()
        )
        QuietTimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = {
                val formattedTime = String.format(Locale.ENGLISH, "%02d:%02d", timeState.hour, timeState.minute)
                onUpdateQuietHoursStart(formattedTime)
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = timeState)
        }
    }

    if (showEndTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = settings.quietHoursEnd.split(":")[0].toInt(),
            initialMinute = settings.quietHoursEnd.split(":")[1].toInt()
        )
        QuietTimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = {
                val formattedTime = String.format(Locale.ENGLISH, "%02d:%02d", timeState.hour, timeState.minute)
                onUpdateQuietHoursEnd(formattedTime)
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = timeState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification_settings),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
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
            // Permission Check (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                if (!permissionState.status.isGranted) {
                    PermissionWarningCard(
                        onAction = {
                            if (permissionState.status.shouldShowRationale) {
                                permissionState.launchPermissionRequest()
                            } else {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                        },
                        buttonText = stringResource(
                            if (permissionState.status.shouldShowRationale) R.string.ok 
                            else R.string.open_settings
                        )
                    )
                }
            }

            // Main Enable Switch
            SettingsCard {
                val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                } else null

                SettingsToggleItem(
                    title = stringResource(R.string.enable_all_notifications),
                    subtitle = stringResource(R.string.enable_all_notifications_desc),
                    icon = Icons.Rounded.Notifications,
                    checked = settings.enableAll,
                    onCheckedChange = { checked ->
                        if (checked && permissionState != null && !permissionState.status.isGranted) {
                            permissionState.launchPermissionRequest()
                        } else {
                            onToggleEnableAll(checked)
                        }
                    },
                    isMain = true
                )
            }

            // Filter Reminders Section
            SectionHeader(title = stringResource(R.string.filter_reminders))
            SettingsCard {
                Column {
                    SettingsToggleItem(
                        title = stringResource(R.string.early_warnings),
                        subtitle = stringResource(R.string.early_warnings_desc),
                        checked = settings.earlyWarnings,
                        enabled = settings.enableAll,
                        onCheckedChange = onToggleEarlyWarnings
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    SettingsToggleItem(
                        title = stringResource(R.string.due_date_reminders),
                        subtitle = stringResource(R.string.due_date_reminders_desc),
                        checked = settings.dueDateReminders,
                        enabled = settings.enableAll,
                        onCheckedChange = onToggleDueDateReminders
                    )
                }
            }

            // Preferences Section
            SectionHeader(title = stringResource(R.string.preferences))
            SettingsCard {
                Column {
                    SettingsToggleItem(
                        title = stringResource(R.string.sound),
                        subtitle = stringResource(R.string.sound_desc),
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        checked = settings.sound,
                        enabled = settings.enableAll,
                        onCheckedChange = {
                            onToggleSound(it)
                            if (it) {
                                try {
                                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                    val r = RingtoneManager.getRingtone(context, notification)
                                    r.play()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    SettingsToggleItem(
                        title = stringResource(R.string.vibration),
                        subtitle = stringResource(R.string.vibration_desc),
                        icon = Icons.Rounded.Vibration,
                        checked = settings.vibration,
                        enabled = settings.enableAll,
                        onCheckedChange = { 
                            onToggleVibration(it)
                            if (it) {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    
                    Column {
                        SettingsToggleItem(
                            title = stringResource(R.string.quiet_hours),
                            subtitle = stringResource(R.string.quiet_hours_desc),
                            icon = Icons.Rounded.DoNotDisturbOn,
                            checked = settings.quietHours,
                            enabled = settings.enableAll,
                            onCheckedChange = onToggleQuietHours
                        )
                        
                        AnimatedVisibility(
                            visible = settings.quietHours && settings.enableAll,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 64.dp, end = 16.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TimeDisplay(
                                    label = stringResource(R.string.start_time),
                                    time = settings.quietHoursStart,
                                    onClick = { showStartTimePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                                TimeDisplay(
                                    label = stringResource(R.string.end_time),
                                    time = settings.quietHoursEnd,
                                    onClick = { showEndTimePicker = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Ad Banner
            AdBanner(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun QuietTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                content()
            }
        }
    )
}

@Composable
fun TimeDisplay(label: String, time: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(56.dp).fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(time, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun NotificationsPreview() {
    WaterFilterTheme {
        NotificationsContent(
            settings = UserSettings(
                enableAll = true,
                earlyWarnings = true,
                dueDateReminders = true,
                sound = true,
                vibration = true,
                quietHours = true,
                quietHoursStart = "22:00",
                quietHoursEnd = "08:00",
                onboardingCompleted = true,
                isLoaded = true
            ),
            onToggleEnableAll = {},
            onToggleEarlyWarnings = {},
            onToggleDueDateReminders = {},
            onToggleSound = {},
            onToggleVibration = {},
            onToggleQuietHours = {},
            onUpdateQuietHoursStart = {},
            onUpdateQuietHoursEnd = {}
        )
    }
}
