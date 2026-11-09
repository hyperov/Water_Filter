package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
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
import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Layers
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandleConfigItem(
    candle: CandleConfigState,
    onSelectionChanged: (SelectionType, Long?) -> Unit,
    onLifespanChanged: ((Lifespan) -> Unit)? = null,
    onRemoveCandle: (() -> Unit)? = null
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showLifespanMenu by remember { mutableStateOf(false) }

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

    val isConfigured = candle.lastChangedTimestamp != null
    val cardBorderColor = if (isConfigured) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    val cardBorderWidth = if (isConfigured) 2.dp else 0.5.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .border(cardBorderWidth, cardBorderColor, RoundedCornerShape(24.dp)),
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
                        .background(
                            if (isConfigured) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isConfigured) Icons.Rounded.Check else Icons.Rounded.Layers,
                        contentDescription = null,
                        tint = if (isConfigured) MaterialTheme.colorScheme.onPrimary 
                               else MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(candle.nameResId),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    Box {
                        AssistChip(
                            onClick = { if (onLifespanChanged != null) showLifespanMenu = true },
                            label = { Text(text = stringResource(candle.lifespan.resId)) },
                            trailingIcon = if (onLifespanChanged != null) {
                                { Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null) }
                            } else null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        
                        DropdownMenu(
                            expanded = showLifespanMenu,
                            onDismissRequest = { showLifespanMenu = false }
                        ) {
                            Lifespan.entries.forEach { lifespanOption ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(lifespanOption.resId)) },
                                    onClick = {
                                        onLifespanChanged?.invoke(lifespanOption)
                                        showLifespanMenu = false
                                    }
                                )
                            }
                        }
                    }

                    if (candle.descriptionResId != 0) {
                        Text(
                            text = stringResource(candle.descriptionResId),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (onRemoveCandle != null) {
                    IconButton(onClick = onRemoveCandle) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.remove_candle),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
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
                    isSelected = candle.selection == SelectionType.TODAY,
                    onClick = { onSelectionChanged(SelectionType.TODAY, System.currentTimeMillis()) },
                    modifier = Modifier.weight(1f)
                )

                // Specific Date
                DateChip(
                    text = stringResource(R.string.specific_date),
                    isSelected = candle.selection == SelectionType.SPECIFIC_DATE,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.CalendarMonth
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Don't Remember
            DateChip(
                text = stringResource(R.string.dont_remember),
                isSelected = candle.selection == SelectionType.DONT_REMEMBER,
                onClick = {
                    val estimatedTimestamp = System.currentTimeMillis() - (candle.lifespan.days.toLong() * 24 * 60 * 60 * 1000 / 2)
                    onSelectionChanged(SelectionType.DONT_REMEMBER, estimatedTimestamp)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Selected Date Footnote
            if (candle.lastChangedTimestamp != null) {
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
                            text = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("ar")).format(Date(candle.lastChangedTimestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    
                    if (candle.selection == SelectionType.DONT_REMEMBER) {
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
fun CandleConfigItemPreview() {
    var candle by remember { mutableStateOf(CandleConfigState(CandleNumber.CANDLE_1, R.string.candle_pp, R.string.candle_pp_desc, Lifespan.MONTHS_3, System.currentTimeMillis() - (Lifespan.MONTHS_3.days.toLong() * 24 * 60 * 60 * 1000 / 2), SelectionType.DONT_REMEMBER)) }
    WaterFilterTheme {
        CandleConfigItem(
            candle = candle,
            onSelectionChanged = { selection, timestamp ->
                candle = candle.copy(selection = selection, lastChangedTimestamp = timestamp)
            }
        )
    }
}
