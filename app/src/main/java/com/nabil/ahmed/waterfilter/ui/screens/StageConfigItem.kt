package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageConfigItem(
    stage: StageConfigState,
    onSelectionChanged: (SelectionType, Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        onSelectionChanged(SelectionType.SPECIFIC_DATE, selectedDate)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(stage.nameResId),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = when (stage.lifespanDays) {
                            90 -> stringResource(R.string.change_every_3_months)
                            180 -> stringResource(R.string.change_every_6_months)
                            365 -> stringResource(R.string.change_every_12_months)
                            730 -> stringResource(R.string.change_every_24_months)
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Changed Today
                DateChip(
                    text = stringResource(R.string.changed_today),
                    isSelected = stage.selection == SelectionType.TODAY,
                    onClick = { onSelectionChanged(SelectionType.TODAY, System.currentTimeMillis()) },
                    modifier = Modifier.weight(1f)
                )

                // Specific Date
                DateChip(
                    text = if (stage.selection == SelectionType.SPECIFIC_DATE && stage.lastChangedTimestamp != null) {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(stage.lastChangedTimestamp!!))
                    } else {
                        stringResource(R.string.specific_date)
                    },
                    isSelected = stage.selection == SelectionType.SPECIFIC_DATE,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.CalendarMonth
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Don't Remember
            DateChip(
                text = stringResource(R.string.dont_remember),
                isSelected = stage.selection == SelectionType.DONT_REMEMBER,
                onClick = { onSelectionChanged(SelectionType.DONT_REMEMBER, null) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun StageConfigItemPreview() {
    var stage by remember { mutableStateOf(StageConfigState(1, R.string.stage_pp, 90)) }
    WaterFilterTheme {
        StageConfigItem(
            stage = stage,
            onSelectionChanged = { selection, timestamp ->
                stage = stage.copy(selection = selection, lastChangedTimestamp = timestamp)
            }
        )
    }
}
