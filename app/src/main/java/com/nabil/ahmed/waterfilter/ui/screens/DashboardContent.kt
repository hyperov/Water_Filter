package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithStages
import com.nabil.ahmed.waterfilter.data.entity.Stage
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.data.util.StageNumber
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@Composable
fun DashboardContent(
    filter: FilterWithStages,
    onMarkAsChanged: (Stage) -> Unit
) {
    val overallHealth = calculateOverallHealth(filter.stages)
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
                
                // Progress Arc
                androidx.compose.foundation.Canvas(modifier = Modifier.size(220.dp)) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * (overallHealth / 100f),
                        useCenter = false,
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "${overallHealth}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = stringResource(R.string.filter_health),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.stages_setup),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // List of Stages
        items(filter.stages.sortedBy { it.stageNumber }) { stage ->
            StageHealthCard(
                stage = stage,
                onMarkAsChanged = { onMarkAsChanged(stage) }
            )
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun DashboardContentPreview() {
    WaterFilterTheme {
        DashboardContent(
            filter = FilterWithStages(
                filter = Filter(1L, "Home Filter", FilterType.STAGES_7),
                stages = listOf(
                    Stage(1L, 1L, StageNumber.STAGE_1, "PP Filter", Lifespan.MONTHS_3, System.currentTimeMillis()),
                    Stage(2L, 1L, StageNumber.STAGE_2, "GAC Filter", Lifespan.MONTHS_6, System.currentTimeMillis())
                )
            ),
            onMarkAsChanged = {}
        )
    }
}
