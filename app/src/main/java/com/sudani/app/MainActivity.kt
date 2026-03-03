package com.sudani.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.sudani.app.ui.screens.KhufashMainScreen
import com.sudani.app.ui.theme.*
import com.sudani.app.viewmodel.SudaniViewModel
import com.sudani.app.data.model.UiState // الاستيراد الصحيح للموديل الموحد
import com.sudani.app.data.worker.ClaimWorker
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // مسجل طلب الصلاحية للتعامل مع أندرويد 13+ لضمان وصول تقارير التجميع
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "تم تفعيل إشعارات الخفاش 🦇", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. طلب صلاحية الإشعارات يدوياً
        askNotificationPermission()

        // 2. جدولة التجميع التلقائي (الساعة 2:01 صباحاً) كما في منطق البوت
        scheduleDailyClaim(this)

        setContent {
            KhufashTheme { 
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = KhufashBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun scheduleDailyClaim(context: android.content.Context) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 1) // الالتزام بتوقيت البوت الدقيق
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val timeDiff = calendar.timeInMillis - System.currentTimeMillis()

        // إعداد طلب العمل الدوري للتجميع لجميع الحسابات الـ 10
        val claimRequest = PeriodicWorkRequestBuilder<ClaimWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "KhufashAutoClaim",
            ExistingPeriodicWorkPolicy.KEEP,
            claimRequest
        )
    }
}

@Composable
fun KhufashTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = KhufashPrimary,
        surface = KhufashSurface,
        background = KhufashBackground,
        tertiary = KhufashAccent,
        onPrimary = Color.White,
        onSurface = TextWhite,
        onBackground = TextWhite
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
fun AppNavigation(viewModel: SudaniViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState() // مراقبة حالة الـ API
    val context = LocalContext.current

    // معالجة ردود السيرفر (نجاح/خطأ) بشكل تلقائي
    LaunchedEffect(uiState) {
        when (val currentState = uiState) {
            is UiState.Success -> Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            is UiState.Error -> Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            else -> {}
        }
    }

    // التبديل التلقائي بين شاشة الدخول والداشبورد
    if (!viewModel.isLoggedIn) {
        LoginScreen(viewModel)
    } else {
        KhufashMainScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: SudaniViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhufashBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("تطبيق الخفاش 🦇", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = KhufashPrimary)
        Text("سجل دخولك لإدارة أرقامك", fontSize = 14.sp, color = TextGray)
        
        Spacer(modifier = Modifier.height(48.dp))

        // منطق تدفق المصادقة المتطابق مع البوت
        if (!viewModel.isOtpSent) {
            OutlinedTextField(
                value = viewModel.msisdn,
                onValueChange = { if (it.length <= 10) viewModel.msisdn = it },
                label = { Text("رقم الهاتف (سوداني)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = KhufashPrimary,
                    unfocusedBorderColor = TextGray,
                    cursorColor = KhufashPrimary
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.sendOtp() }, // استدعاء الدالة من الـ ViewModel
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KhufashPrimary)
            ) {
                Text("إرسال رمز التحقق", fontWeight = FontWeight.Bold)
            }
        } else {
            OutlinedTextField(
                value = viewModel.otp,
                onValueChange = { viewModel.otp = it },
                label = { Text("رمز التحقق (6 أرقام)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.verifyOtp() }, // إكمال الـ Onboarding
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("تأكيد وتسجيل الدخول", fontWeight = FontWeight.Bold)
            }
            
            TextButton(onClick = { viewModel.isOtpSent = false }) {
                Text("تغيير الرقم؟", color = TextGray)
            }
        }
    }
}
