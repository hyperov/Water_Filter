package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class HealthInfo(
    val healthPercentage: Float,
    val daysRemaining: Int,
    val statusText: String
)

@Composable
fun getSmartCountdownText(days: Int): String {
    return when {
        days == 0 -> stringResource(R.string.overdue)
        days == 1 -> "يجب التغيير غداً"
        days < 7 -> "باقي $days أيام (خلال أسبوع)"
        days < 30 -> "باقي $days يوم"
        days < 60 -> "باقي شهر وبضعة أيام"
        else -> {
            val months = days / 30
            "باقي $months أشهر تقريباً"
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun calculateCandleHealth(candle: Candle): HealthInfo {
    val currentTime = System.currentTimeMillis()
    val elapsedTime = currentTime - candle.lastChangedTimestamp
    val lifespanTime = TimeUnit.DAYS.toMillis(candle.lifespanDays.days.toLong())
    
    val remainingTime = lifespanTime - elapsedTime
    val healthPercentage = (remainingTime.toFloat() / lifespanTime.toFloat() * 100).coerceIn(0f, 100f)
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(remainingTime).toInt().coerceAtLeast(0)
    
    return HealthInfo(
        healthPercentage = healthPercentage,
        daysRemaining = daysRemaining,
        statusText = getSmartCountdownText(daysRemaining)
    )
}

fun calculateOverallHealth(candles: List<Candle>): Int {
    if (candles.isEmpty()) return 100
    // Use Double for intermediate calculation to avoid precision issues
    val totalHealthPercentage = candles.sumOf { 
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - it.lastChangedTimestamp
        val lifespanTime = TimeUnit.DAYS.toMillis(it.lifespanDays.days.toLong())
        val remainingTime = lifespanTime - elapsedTime
        (remainingTime.toDouble() / lifespanTime.toDouble() * 100).coerceIn(0.0, 100.0)
    }
    return (totalHealthPercentage / candles.size).roundToInt()
}

@Composable
fun getHealthColor(percentage: Int): Color {
    return when {
        percentage > 60 -> Color(0xFF4CAF50) // Green (Safe)
        percentage > 25 -> Color(0xFFFFC107) // Amber (Warning)
        percentage > 0 -> Color(0xFFFF9800)  // Orange (Critical)
        else -> MaterialTheme.colorScheme.error // Red (Overdue)
    }
}
