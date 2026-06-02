package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nabil.ahmed.waterfilter.data.entity.Candle
import java.util.concurrent.TimeUnit

data class HealthInfo(
    val healthPercentage: Float,
    val daysRemaining: Int
)

fun calculateCandleHealth(candle: Candle): HealthInfo {
    val currentTime = System.currentTimeMillis()
    val elapsedTime = currentTime - candle.lastChangedTimestamp
    val lifespanTime = TimeUnit.DAYS.toMillis(candle.lifespanDays.days.toLong())
    
    val remainingTime = lifespanTime - elapsedTime
    val healthPercentage = (remainingTime.toFloat() / lifespanTime.toFloat() * 100).coerceIn(0f, 100f)
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(remainingTime).toInt().coerceAtLeast(0)
    
    return HealthInfo(healthPercentage, daysRemaining)
}

fun calculateOverallHealth(candles: List<Candle>): Int {
    if (candles.isEmpty()) return 100
    val totalHealth = candles.sumOf { calculateCandleHealth(it).healthPercentage.toInt() }
    return totalHealth / candles.size
}

@Composable
fun getHealthColor(percentage: Int): Color {
    return when {
        percentage > 60 -> Color(0xFF4CAF50) // Green
        percentage > 20 -> Color(0xFFFFC107) // Amber
        else -> MaterialTheme.colorScheme.error
    }
}
