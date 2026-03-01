package com.sudani.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.sudani.app.ui.theme.*

// تعريف الشاشات السفلية بعد التعديل (3 تابات فقط)
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("الرئيسية", Icons.Default.Home)
    object Services : BottomNavItem("الخدمات", Icons.Default.List)
    object Settings : BottomNavItem("الإعدادات", Icons.Default.Settings)
}

@Composable
fun KhufashMainScreen() {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }
    
    // القائمة الجديدة المحدثة
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Services,
        BottomNavItem.Settings
    )

    Scaffold(
        containerColor = KhufashBackground,
        bottomBar = {
            NavigationBar(
                containerColor = KhufashSurface,
                contentColor = TextWhite
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KhufashBackground,
                            selectedTextColor = KhufashPrimary,
                            indicatorColor = KhufashPrimary,
                            unselectedIconColor = TextGray,
                            unselectedTextColor = TextGray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // هنا بيتم عرض الشاشة بناءً على التبويب المختار
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                BottomNavItem.Home -> HomeScreenKhufash() // الشاشة الرئيسية اللي عملناها
                BottomNavItem.Services -> ServicesScreenKhufash() // شاشة الخدمات الجديدة
                BottomNavItem.Settings -> Text("شاشة الإعدادات قيد الإنشاء 🦇", color = TextWhite) // Placeholder للإعدادات
            }
        }
    }
}
