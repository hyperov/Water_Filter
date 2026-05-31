package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Stage
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@Composable
fun StageHealthCard(
    stage: Stage,
    onMarkAsChanged: () -> Unit
) {
    val healthInfo = calculateStageHealth(stage)
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
                        text = stage.name,
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
                            fontWeight = if (healthInfo.daysRemaining <= 0) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(healthColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = healthColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { healthInfo.healthPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = healthColor,
                trackColor = Color.LightGray.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onMarkAsChanged,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.mark_as_changed),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StageHealthCardPreview() {
    WaterFilterTheme {
        StageHealthCard(
            stage = Stage(
                id = 1,
                filterId = 1,
                stageNumber = 1,
                name = "PP Filter",
                lifespanDays = 90,
                lastChangedTimestamp = System.currentTimeMillis()
            ),
            onMarkAsChanged = {}
        )
    }
}
