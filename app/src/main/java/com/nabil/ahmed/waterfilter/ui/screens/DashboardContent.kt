package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithCandles
import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment

@Composable
fun DashboardContent(
    filter: FilterWithCandles,
    onMarkAsChanged: (Candle) -> Unit,
    onEditDate: (Candle) -> Unit,
    onRenameCandle: (Candle) -> Unit,
    onEditLifespan: (Candle) -> Unit = {},
    onAddCandle: (() -> Unit)? = null,
    onDeleteCandle: ((Candle) -> Unit)? = null
) {
    val overallHealth = calculateOverallHealth(filter.candles)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Filter Efficiency Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4C44))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.overall_efficiency),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0D3CB))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${overallHealth}% ${stringResource(R.string.efficiency_excellent)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.candle_updated_today),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF88BDB1))
                        )
                    }

                    Surface(
                        color = Color(0xFF1E5B53),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Header: Candle Stages
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.active_candles_count, filter.candles.size),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6A8B83))
                )
                Text(
                    text = stringResource(R.string.candle_stages),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D2823)
                    )
                )
            }
        }

        // Individual Candle Cards
        items(filter.candles, key = { it.id }) { candle ->
            CandleHealthCard(
                candle = candle,
                onMarkAsChanged = { onMarkAsChanged(candle) },
                onEditDate = { onEditDate(candle) },
                onRename = { onRenameCandle(candle) },
                onEditLifespan = { onEditLifespan(candle) },
                onDelete = if (filter.candles.size > 1 && onDeleteCandle != null) {
                    { onDeleteCandle(candle) }
                } else null
            )
        }

        // Add Candle Button if candles count < 7
        if (filter.candles.size < 7 && onAddCandle != null) {
            item {
                OutlinedButton(
                    onClick = onAddCandle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.add_new_candle))
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun DashboardContentPreview() {
    WaterFilterTheme {
        DashboardContent(
            filter = FilterWithCandles(
                filter = Filter(1L, "Home Filter", FilterType.CANDLES_3),
                candles = listOf(
                    Candle(1L, 1L, CandleNumber.CANDLE_1, "PP Filter", Lifespan.MONTHS_3, System.currentTimeMillis()),
                    Candle(2L, 1L, CandleNumber.CANDLE_2, "GAC Filter", Lifespan.MONTHS_6, System.currentTimeMillis())
                )
            ),
            onMarkAsChanged = {},
            onEditDate = {},
            onRenameCandle = {}
        )
    }
}
