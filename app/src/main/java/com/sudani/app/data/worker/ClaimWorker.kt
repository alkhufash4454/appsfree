package com.sudani.app.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sudani.app.data.model.OnboardingData
import com.sudani.app.data.repository.SudaniRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ClaimWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val repository = SudaniRepository()
    private val sharedPrefs = appContext.getSharedPreferences("khufash_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    override suspend fun doWork(): Result = coroutineScope {
        val json = sharedPrefs.getString("accounts_list", null) ?: return@coroutineScope Result.success()
        val type = object : TypeToken<List<OnboardingData>>() {}.type
        val accounts: List<OnboardingData> = gson.fromJson(json, type)

        var totalGained = 0
        var successCount = 0

        val tasks = accounts.map { account ->
            async {
                try {
                    val claimResponse = repository.claimPoints(account.customerId ?: "", account.token ?: "", "0")
                    if (claimResponse.isSuccessful) {
                        successCount++
                        totalGained += 10
                    }
                } catch (e: Exception) { }
            }
        }
        tasks.awaitAll()

        if (successCount > 0) {
            showNotification(successCount, totalGained)
        }
        Result.success()
    }

    private fun showNotification(count: Int, points: Int) {
        val channelId = "khufash_claim"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "تجميع الخفاش", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("🦇 تم تجميع نقاط الخفاش")
            .setContentText("نجح التجميع لـ $count أرقام. الإجمالي المكتسب: +$points نقطة")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
