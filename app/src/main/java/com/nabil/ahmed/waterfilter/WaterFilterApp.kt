package com.nabil.ahmed.waterfilter

import android.app.Application
import android.webkit.WebView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.google.android.gms.ads.MobileAds
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

        // Fix for WebView crash in multi-process apps
        val processName = getProcessName()
        if (packageName != processName) {
            WebView.setDataDirectorySuffix(processName)
        }
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

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
            reminderRequest,
        )
    }
}
