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
import com.sudani.app.viewmodel.UiState
import com.sudani.app.data.worker.ClaimWorker
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // مسجل طلب الصلاحية
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "تم تفعيل إشعارات الخفاش 🦇", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. طلب الصلاحية يدويًا
        askNotificationPermission()

        // 2. جدولة التجميع التلقائي
        scheduleDailyClaim(this)

        setContent {
            // الحل الجذري: تأكد أن KhufashTheme معرفة هنا أو استبدلها بـ SudaniAppTheme
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
            // تصحيح الخطأ الإملائي: POST_NOTIFICATIONS بدلاً من POST_NOT_NOTIFICATIONS
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
            set(Calendar.MINUTE, 1)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val timeDiff = calendar.timeInMillis - System.currentTimeMillis()

        val claimRequest = PeriodicWorkRequestBuilder<ClaimWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "KhufashAutoClaim",
            ExistingPeriodicWorkPolicy.KEEP,
            claimRequest
        )
    }
}

// تعريف الثيم مباشرة لحل مشكلة Unresolved reference
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
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        val currentState = uiState 
        when (currentState) {
            is UiState.Success -> Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            is UiState.Error -> Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            else -> {}
        }
    }

    if (!viewModel.isLoggedIn) {
        LoginScreen(viewModel)
    } else {
        KhufashMainScreen(viewModel)
    }
}

// الحل الجذري: تعريف شاشة الدخول هنا لضمان وجود المرجع
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
        Spacer(modifier = Modifier.height(48.dp))

        if (!viewModel.isOtpSent) {
            OutlinedTextField(
                value = viewModel.msisdn,
                onValueChange = { if (it.length <= 10) viewModel.msisdn = it },
                label = { Text("رقم الهاتف") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.sendOtp() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("إرسال رمز التحقق")
            }
        } else {
            OutlinedTextField(
                value = viewModel.otp,
                onValueChange = { viewModel.otp = it },
                label = { Text("رمز التحقق") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.verifyOtp() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("تأكيد الدخول")
            }
        }
    }
}
