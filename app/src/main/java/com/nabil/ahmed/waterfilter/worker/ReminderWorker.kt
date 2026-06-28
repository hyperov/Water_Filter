package com.nabil.ahmed.waterfilter.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.dao.FilterDao
import com.nabil.ahmed.waterfilter.data.repository.SettingsRepository
import com.nabil.ahmed.waterfilter.data.repository.UserSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: FilterDao,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val settings = settingsRepository.settingsFlow.first()
        
        if (!settings.enableAll || isQuietHours(settings)) {
            return Result.success()
        }

        val filters = dao.getAllFiltersWithCandlesSync()
        
        val now = System.currentTimeMillis()
        val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)

        filters.forEach { filterWithStages ->
            filterWithStages.candles.forEach { stage ->
                if (stage.lastChangedTimestamp != 0L) {
                    val lifespanMillis = TimeUnit.DAYS.toMillis(stage.lifespanDays.days.toLong())
                    val expirationTime = stage.lastChangedTimestamp + lifespanMillis
                    val timeUntilExpiration = expirationTime - now

                    // Exact Day Reminder
                    if (settings.dueDateReminders && timeUntilExpiration in 0..TimeUnit.HOURS.toMillis(24)) {
                        sendNotification(
                            applicationContext,
                            stage.id.toInt(),
                            applicationContext.getString(R.string.reminder_notification_title),
                            applicationContext.getString(R.string.reminder_notification_body, filterWithStages.filter.name, stage.name),
                            settings
                        )
                    }
                    // 7 Days Before Reminder
                    else if (settings.earlyWarnings && timeUntilExpiration in sevenDaysMillis..(sevenDaysMillis + TimeUnit.HOURS.toMillis(24))) {
                        sendNotification(
                            applicationContext,
                            stage.id.toInt() + 1000,
                            applicationContext.getString(R.string.reminder_notification_title),
                            applicationContext.getString(R.string.early_reminder_notification_body, filterWithStages.filter.name, stage.name),
                            settings
                        )
                    }
                }
            }
        }

        return Result.success()
    }

    private fun isQuietHours(settings: UserSettings): Boolean {
        if (!settings.quietHours) return false
        
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute
        
        val startParts = settings.quietHoursStart.split(":")
        val startInMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
        
        val endParts = settings.quietHoursEnd.split(":")
        val endInMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
        
        return if (startInMinutes <= endInMinutes) {
            currentTimeInMinutes in startInMinutes..endInMinutes
        } else {
            currentTimeInMinutes !in (endInMinutes + 1)..<startInMinutes
        }
    }

    private fun sendNotification(context: Context, id: Int, title: String, message: String, settings: UserSettings) {
        val channelId = "filter_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val importance = if (settings.sound) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(
            channelId,
            context.getString(R.string.filter_reminders),
            importance
        ).apply {
            if (!settings.vibration) {
                enableVibration(false)
                vibrationPattern = null
            }
        }
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (settings.sound) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)

        if (!settings.sound) {
            notificationBuilder.setSilent(true)
        }
        
        if (!settings.vibration) {
            notificationBuilder.setVibrate(longArrayOf(0))
        }

        notificationManager.notify(id, notificationBuilder.build())
    }
}
