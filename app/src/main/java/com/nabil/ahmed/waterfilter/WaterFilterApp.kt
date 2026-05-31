package com.nabil.ahmed.waterfilter

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.nabil.ahmed.waterfilter.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WaterFilterApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleReminders()
    }

    private fun scheduleReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "filter_reminders",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}
