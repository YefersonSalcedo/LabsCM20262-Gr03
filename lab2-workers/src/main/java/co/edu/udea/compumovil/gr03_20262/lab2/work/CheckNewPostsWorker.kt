package co.edu.udea.compumovil.gr03_20262.lab2.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import retrofit2.HttpException
import java.io.IOException

class CheckNewPostsWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val count = checker.refreshAndCountNew()
            if (count > 0) {
                val notifyWorkRequest = OneTimeWorkRequestBuilder<NotifyWorker>()
                    .setInputData(workDataOf("count" to count))
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(notifyWorkRequest)
            }
            Result.success()
        } catch (e: IOException) {
            Result.retry()
        } catch (e: HttpException) {
            if (e.code() >= 500) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        var checker: NewsChecker = FakeNewsChecker
    }
}
