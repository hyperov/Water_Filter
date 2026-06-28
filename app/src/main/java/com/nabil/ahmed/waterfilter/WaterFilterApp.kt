package com.nabil.ahmed.waterfilter

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.google.android.gms.ads.MobileAds
import com.nabil.ahmed.waterfilter.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        
        // Initialize Mobile Ads SDK
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@WaterFilterApp) {}
        }

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
