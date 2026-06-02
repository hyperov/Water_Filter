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
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.data.util.StageNumber
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
                        text = stringResource(stage.lifespan.resId),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = stringResource(stage.descriptionResId),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.last_maintenance_question),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

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
                    text = stringResource(R.string.specific_date),
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
                onClick = {
                    val estimatedTimestamp = System.currentTimeMillis() - (stage.lifespan.days.toLong() * 24 * 60 * 60 * 1000 / 2)
                    onSelectionChanged(SelectionType.DONT_REMEMBER, estimatedTimestamp)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Selected Date Footnote
            if (stage.lastChangedTimestamp != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.selected_date) + ": ",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("ar")).format(Date(stage.lastChangedTimestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    
                    if (stage.selection == SelectionType.DONT_REMEMBER) {
                        Text(
                            text = stringResource(R.string.estimated_date_note),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(locale = "ar")
@Composable
fun StageConfigItemPreview() {
    var stage by remember { mutableStateOf(StageConfigState(StageNumber.STAGE_1, R.string.stage_pp, R.string.stage_pp_desc, Lifespan.MONTHS_3, System.currentTimeMillis() - (Lifespan.MONTHS_3.days.toLong() * 24 * 60 * 60 * 1000 / 2), SelectionType.DONT_REMEMBER)) }
    WaterFilterTheme {
        StageConfigItem(
            stage = stage,
            onSelectionChanged = { selection, timestamp ->
                stage = stage.copy(selection = selection, lastChangedTimestamp = timestamp)
            }
        )
    }
}
