package com.example.classroomassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(current: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        Triple(NavRoute.Home.route, "首页", Icons.Outlined.Home),
        Triple(NavRoute.Schedule.route, "课程表", Icons.Outlined.Schedule),
        Triple(NavRoute.Countdown.route, "倒计时", Icons.Outlined.Timer),
        Triple(NavRoute.Calendar.route, "日历", Icons.Outlined.CalendarMonth),
        Triple(NavRoute.Settings.route, "设置", Icons.Outlined.Settings)
    )
    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = current == route,
                onClick = { onNavigate(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}
