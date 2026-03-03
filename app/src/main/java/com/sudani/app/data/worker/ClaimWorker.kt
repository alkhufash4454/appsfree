package com.sudani.app.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
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
        // 1. جلب قائمة الحسابات الـ 10 المحفوظة
        val json = sharedPrefs.getString("accounts_list", null) ?: return@coroutineScope Result.success()
        val type = object : TypeToken<List<OnboardingData>>() {}.type
        val accounts: List<OnboardingData> = gson.fromJson(json, type)

        var totalGained = 0
        var successCount = 0

        // 2. تنفيذ التجميع المتوازي (محاكاة منطق البوت لضمان السرعة)
        val tasks = accounts.map { account ->
            async {
                try {
                    val msisdn = account.customerId ?: ""
                    val token = account.token ?: ""
                    
                    // السيرفر يحتاج قيمة النقاط الحالية كـ String (بدون كسور)
                    // في التجميع التلقائي نرسل "0" كبداية إذا لم تتوفر القيمة المحدثة
                    val lastPoints = "0" 

                    // استدعاء التجميع بالهيدرز الكاملة
                    val claimResponse = repository.claimPoints(
                        msisdn = msisdn,
                        token = token,
                        userData = account,
                        points = lastPoints // تم توحيد المسمى مع الـ Repository
                    )

                    if (claimResponse.isSuccessful && claimResponse.body()?.responseCode == "200") {
                        successCount++
                        totalGained += 10 
                    }
                } catch (e: Exception) {
                    // فشل حساب واحد لا يعطل البقية
                }
            }
        }

        // انتظار اكتمال التجميع لجميع الأرقام
        tasks.awaitAll()

        // 3. إظهار إشعار الخفاش النهائي
        if (successCount > 0) {
            showNotification(successCount, totalGained)
        }
        
        Result.success()
    }

    private fun showNotification(count: Int, points: Int) {
        val channelId = "khufash_claim"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // إنشاء قناة الإشعارات لأندرويد 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, 
                "تجميع الخفاش 🦇", 
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // بناء الإشعار
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.star_on) 
            .setContentTitle("🦇 تم تجميع نقاط الخفاش")
            .setContentText("نجح التجميع لـ $count أرقام. الإجمالي المكتسب: +$points نقطة")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // التحقق من صلاحيات الإشعارات لأندرويد 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, 
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                return 
            }
        }

        notificationManager.notify(1, notification)
    }
}
