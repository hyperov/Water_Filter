package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandleHealthCard(
    candle: Candle,
    onMarkAsChanged: () -> Unit,
    onEditDate: () -> Unit
) {
    val healthInfo = calculateCandleHealth(candle)
    val healthColor = getHealthColor(healthInfo.healthPercentage.roundToInt())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = healthColor.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border =
            BorderStroke(2.dp, healthColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candle.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = healthColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = healthInfo.statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = healthColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(text = stringResource(R.string.mark_as_changed)) } },
                        state = rememberTooltipState()
                    ) {
                        Button(
                            onClick = onMarkAsChanged,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = healthColor,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.mark_as_changed))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            val animatedProgress by animateFloatAsState(
                targetValue = healthInfo.healthPercentage / 100f,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                ),
                label = "health_progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = healthColor,
                trackColor = healthColor.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${healthInfo.healthPercentage.roundToInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = healthColor
                    )
                )
                
                Surface(
                    onClick = onEditDate,
                    color = healthColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit),
                            modifier = Modifier.size(18.dp),
//                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.last_maintenance_date) + ": " + formatDate(candle.lastChangedTimestamp),
                            style = MaterialTheme.typography.bodyMedium.copy(
//                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true,locale = "ar")
@Composable
fun CandleHealthCardPreview() {
    WaterFilterTheme {
        CandleHealthCard(
            candle = Candle(
                id = 1L,
                filterId = 1L,
                candleNumber = CandleNumber.CANDLE_1,
                name = "PP Filter",
                lifespanDays = Lifespan.MONTHS_6,
                lastChangedTimestamp = System.currentTimeMillis()
            ),
            onMarkAsChanged = {},
            onEditDate = {}
        )
    }
}
