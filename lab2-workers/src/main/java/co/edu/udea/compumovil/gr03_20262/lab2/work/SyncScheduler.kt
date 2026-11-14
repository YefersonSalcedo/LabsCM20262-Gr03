package co.edu.udea.compumovil.gr03_20262.lab2.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit

object SyncScheduler {

    private val netConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodic(ctx: Context) {
        val request = PeriodicWorkRequestBuilder<CheckNewPostsWorker>(15, TimeUnit.MINUTES)
            .setConstraints(netConstraints)
            .build()

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun syncNow(ctx: Context): UUID {
        val request = OneTimeWorkRequestBuilder<CheckNewPostsWorker>()
            .setConstraints(netConstraints)
            .build()

        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "sync_now",
            ExistingWorkPolicy.REPLACE,
            request
        )

        return request.id
    }
}
