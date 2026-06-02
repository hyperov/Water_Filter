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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: FilterDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
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
                    if (timeUntilExpiration in 0..TimeUnit.HOURS.toMillis(24)) {
                        sendNotification(
                            applicationContext,
                            stage.id.toInt(),
                            applicationContext.getString(R.string.reminder_notification_title),
                            applicationContext.getString(R.string.reminder_notification_body, filterWithStages.filter.name, stage.name)
                        )
                    }
                    // 7 Days Before Reminder
                    else if (timeUntilExpiration in sevenDaysMillis..(sevenDaysMillis + TimeUnit.HOURS.toMillis(24))) {
                        sendNotification(
                            applicationContext,
                            stage.id.toInt() + 1000,
                            applicationContext.getString(R.string.reminder_notification_title),
                            applicationContext.getString(R.string.early_reminder_notification_body, filterWithStages.filter.name, stage.name)
                        )
                    }
                }
            }
        }

        return Result.success()
    }

    private fun sendNotification(context: Context, id: Int, title: String, message: String) {
        val channelId = "filter_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.filter_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
