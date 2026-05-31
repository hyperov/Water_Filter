package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nabil.ahmed.waterfilter.data.entity.Stage
import java.util.concurrent.TimeUnit

data class HealthInfo(val healthPercentage: Float, val daysRemaining: Long)

fun calculateStageHealth(stage: Stage): HealthInfo {
    if (stage.lastChangedTimestamp == 0L) return HealthInfo(0f, 0L)
    
    val now = System.currentTimeMillis()
    val elapsedMillis = now - stage.lastChangedTimestamp
    val lifespanMillis = TimeUnit.DAYS.toMillis(stage.lifespanDays.toLong())
    
    val remainingMillis = lifespanMillis - elapsedMillis
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(remainingMillis)
    
    val healthPercentage = ((remainingMillis.toFloat() / lifespanMillis.toFloat()) * 100).coerceIn(0f, 100f)
    
    return HealthInfo(healthPercentage, daysRemaining)
}

fun calculateOverallHealth(stages: List<Stage>): Int {
    if (stages.isEmpty()) return 0
    val totalHealth = stages.sumOf { calculateStageHealth(it).healthPercentage.toDouble() }
    return (totalHealth / stages.size).toInt()
}

@Composable
fun getHealthColor(health: Int): Color {
    return when {
        health > 70 -> MaterialTheme.colorScheme.primary
        health > 30 -> Color(0xFFFFA000) // Amber
        else -> MaterialTheme.colorScheme.error
    }
}
