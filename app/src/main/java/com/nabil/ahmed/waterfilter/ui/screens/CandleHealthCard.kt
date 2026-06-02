package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@Composable
fun CandleHealthCard(
    candle: Candle,
    onMarkAsChanged: () -> Unit
) {
    val healthInfo = calculateCandleHealth(candle)
    val healthColor = getHealthColor(healthInfo.healthPercentage.toInt())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candle.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (healthInfo.daysRemaining > 0) {
                            stringResource(R.string.remaining_days, healthInfo.daysRemaining)
                        } else {
                            stringResource(R.string.overdue)
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (healthInfo.daysRemaining > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                            fontWeight = if (healthInfo.daysRemaining > 0) FontWeight.Normal else FontWeight.Bold
                        )
                    )
                }

                FilledTonalIconButton(
                    onClick = onMarkAsChanged,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = healthColor.copy(alpha = 0.1f),
                        contentColor = healthColor
                    )
                ) {
                    Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = stringResource(R.string.mark_as_changed))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val animatedProgress by animateFloatAsState(
                targetValue = healthInfo.healthPercentage / 100f,
                label = "health_progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = healthColor,
                trackColor = healthColor.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${healthInfo.healthPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = healthColor
                    )
                )
                Text(
                    text = stringResource(R.string.candle) + " " + candle.candleNumber.value,
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CandleHealthCardPreview() {
    WaterFilterTheme {
        CandleHealthCard(
            candle = Candle(
                id = 1L,
                filterId = 1L,
                candleNumber = CandleNumber.CANDLE_1,
                name = "PP Filter",
                lifespanDays = Lifespan.MONTHS_3,
                lastChangedTimestamp = System.currentTimeMillis()
            ),
            onMarkAsChanged = {}
        )
    }
}
