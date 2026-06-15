package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DoneAll
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
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandlesConfigurationScreen(
    filterName: String,
    filterType: FilterType,
    onSave: (List<CandleConfigState>) -> Unit,
    onBack: () -> Unit
) {
    val candles = remember {
        mutableStateListOf<CandleConfigState>().apply {
            add(CandleConfigState(CandleNumber.CANDLE_1, R.string.candle_pp, R.string.candle_pp_desc, Lifespan.MONTHS_3))
            if (filterType.candlesCount >= 3) {
                add(CandleConfigState(CandleNumber.CANDLE_2, R.string.candle_gac, R.string.candle_gac_desc, Lifespan.MONTHS_6))
                add(CandleConfigState(CandleNumber.CANDLE_3, R.string.candle_cto, R.string.candle_cto_desc, Lifespan.MONTHS_6))
            }
            if (filterType.candlesCount >= 5) {
                add(CandleConfigState(CandleNumber.CANDLE_4, R.string.candle_ro, R.string.candle_ro_desc, Lifespan.MONTHS_24))
                add(CandleConfigState(CandleNumber.CANDLE_5, R.string.candle_post_carbon, R.string.candle_post_carbon_desc, Lifespan.MONTHS_12))
            }
            if (filterType.candlesCount >= 7) {
                add(CandleConfigState(CandleNumber.CANDLE_6, R.string.candle_mineral, R.string.candle_mineral_desc, Lifespan.MONTHS_12))
                add(CandleConfigState(CandleNumber.CANDLE_7, R.string.candle_alkaline, R.string.candle_alkaline_desc, Lifespan.MONTHS_12))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.candles_setup),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.candles_setup),
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = stringResource(R.string.step_2_of_2),
                            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.candles_setup_desc),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Set All to Today Shortcut
                    OutlinedButton(
                        onClick = {
                            val today = System.currentTimeMillis()
                            for (i in candles.indices) {
                                candles[i] = candles[i].copy(
                                    selection = SelectionType.TODAY,
                                    lastChangedTimestamp = today
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.set_all_today))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = stringResource(R.string.last_maintenance_question),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            items(candles, key = { it.candleNumber.value }) { candle ->
                CandleConfigItem(
                    candle = candle,
                    onSelectionChanged = { selection, timestamp ->
                        val index = candles.indexOfFirst { it.candleNumber == candle.candleNumber }
                        if (index != -1) {
                            candles[index] = candle.copy(
                                selection = selection,
                                lastChangedTimestamp = timestamp
                            )
                        }
                    }
                )
            }

            item {
                Button(
                    onClick = { onSave(candles) },
                    enabled = candles.all { it.selection != SelectionType.NONE },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.save_and_continue),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun CandlesConfigurationPreview() {
    WaterFilterTheme {
        CandlesConfigurationScreen(filterName = "Home", filterType = FilterType.CANDLES_1, onSave = {}, onBack = {})
    }
}
