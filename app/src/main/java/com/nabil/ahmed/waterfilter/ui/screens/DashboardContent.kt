package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

@Composable
fun DashboardContent(
    filter: FilterWithCandles,
    onMarkAsChanged: (Candle) -> Unit
) {
    val overallHealth = calculateOverallHealth(filter.candles)
    val progressColor = getHealthColor(overallHealth)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Circular Health Indicator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track
                androidx.compose.foundation.Canvas(modifier = Modifier.size(220.dp)) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                val animatedHealth by animateFloatAsState(
                    targetValue = overallHealth.toFloat(),
                    animationSpec = tween(durationMillis = 1000),
                    label = "health_anim"
                )

                // Progress Arc
                androidx.compose.foundation.Canvas(modifier = Modifier.size(220.dp)) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = animatedHealth * 3.6f,
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${overallHealth}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 56.sp,
                            color = progressColor
                        )
                    )
                    Text(
                        text = stringResource(R.string.filter_health),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Section Header
        item {
            SectionHeader(
                title = stringResource(R.string.candles_history)
            )
        }

        // Individual Candle Cards
        items(filter.candles, key = { it.id }) { candle ->
            CandleHealthCard(
                candle = candle,
                onMarkAsChanged = { onMarkAsChanged(candle) }
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun DashboardContentPreview() {
    WaterFilterTheme {
        DashboardContent(
            filter = FilterWithCandles(
                filter = Filter(1L, "Home Filter", FilterType.CANDLES_7),
                candles = listOf(
                    Candle(1L, 1L, CandleNumber.CANDLE_1, "PP Filter", Lifespan.MONTHS_3, System.currentTimeMillis()),
                    Candle(2L, 1L, CandleNumber.CANDLE_2, "GAC Filter", Lifespan.MONTHS_6, System.currentTimeMillis())
                )
            ),
            onMarkAsChanged = {}
        )
    }
}
