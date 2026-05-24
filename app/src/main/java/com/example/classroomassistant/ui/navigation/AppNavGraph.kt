package com.example.classroomassistant.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.classroomassistant.AppContainer
import com.example.classroomassistant.ui.screens.calendar.CalendarScreen
import com.example.classroomassistant.ui.screens.calendar.CalendarViewModel
import com.example.classroomassistant.ui.screens.calendar.CalendarVmFactory
import com.example.classroomassistant.ui.screens.calendar.EventEditScreen
import com.example.classroomassistant.ui.screens.countdown.CountdownScreen
import com.example.classroomassistant.ui.screens.countdown.CountdownViewModel
import com.example.classroomassistant.ui.screens.countdown.CountdownVmFactory
import com.example.classroomassistant.ui.screens.home.HomeScreen
import com.example.classroomassistant.ui.screens.home.HomeViewModel
import com.example.classroomassistant.ui.screens.home.HomeVmFactory
import com.example.classroomassistant.ui.screens.schedule.CourseDetailScreen
import com.example.classroomassistant.ui.screens.schedule.CourseEditScreen
import com.example.classroomassistant.ui.screens.schedule.ScheduleScreen
import com.example.classroomassistant.ui.screens.schedule.ScheduleViewModel
import com.example.classroomassistant.ui.screens.schedule.ScheduleVmFactory
import com.example.classroomassistant.ui.screens.settings.SettingsScreen
import com.example.classroomassistant.ui.screens.settings.SettingsViewModel
import com.example.classroomassistant.ui.screens.settings.SettingsVmFactory

@Composable
fun AppNavGraph(container: AppContainer) {
    val nav = rememberNavController()
    val scheduleVm: ScheduleViewModel = viewModel(
        factory = ScheduleVmFactory(container.courseRepository, container.reminderRepository)
    )
    val homeVm: HomeViewModel = viewModel(
        factory = HomeVmFactory(container.courseRepository, container.calendarRepository, container.settingsRepository)
    )
    val countdownVm: CountdownViewModel = viewModel(factory = CountdownVmFactory(container.reminderRepository))
    val calendarVm: CalendarViewModel = viewModel(factory = CalendarVmFactory(container.calendarRepository))
    val settingsVm: SettingsViewModel = viewModel(factory = SettingsVmFactory(container.settingsRepository))

    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route ?: NavRoute.Home.route

    Scaffold(bottomBar = {
        if (!current.contains("edit") && !current.contains("detail")) {
            BottomNavBar(current) { route -> nav.navigate(route) }
        }
    }) { padding ->
        NavHost(nav, startDestination = NavRoute.Home.route, modifier = Modifier.padding(padding)) {
            composable(NavRoute.Home.route) {
                HomeScreen(homeVm, onGoCountdown = { nav.navigate(NavRoute.Countdown.route) }, onCourseClick = { nav.navigate("course_detail/$it") })
            }
            composable(NavRoute.Schedule.route) {
                ScheduleScreen(scheduleVm, onAdd = { nav.navigate("course_edit/0") }, onDetail = { nav.navigate("course_detail/$it") })
            }
            composable(NavRoute.Countdown.route) {
                val first = remember(scheduleVm.courses.value) { scheduleVm.courses.value.firstOrNull() }
                if (first != null && countdownVm.uiState.value.course == null) countdownVm.start(first)
                CountdownScreen(countdownVm) { nav.navigate(NavRoute.Home.route) }
            }
            composable(NavRoute.Calendar.route) {
                CalendarScreen(calendarVm, onAdd = { nav.navigate("event_edit/$it") })
            }
            composable(NavRoute.Settings.route) { SettingsScreen(settingsVm) }
            composable("course_edit/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { back ->
                val id = back.arguments?.getLong("id") ?: 0L
                val existing = scheduleVm.courses.value.firstOrNull { it.id == id }
                CourseEditScreen(existing, scheduleVm) { nav.popBackStack() }
            }
            composable("course_detail/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { back ->
                val id = back.arguments?.getLong("id") ?: 0L
                val course = scheduleVm.courses.value.firstOrNull { it.id == id }
                CourseDetailScreen(
                    course,
                    scheduleVm,
                    onStart = { course?.let { countdownVm.start(it) }; nav.navigate(NavRoute.Countdown.route) },
                    onBack = { nav.popBackStack() },
                    onEditCourse = { nav.navigate("course_edit/$it") }
                )
            }
            composable("event_edit/{date}", arguments = listOf(navArgument("date") { type = NavType.StringType })) { back ->
                val date = back.arguments?.getString("date") ?: java.time.LocalDate.now().toString()
                EventEditScreen(date, calendarVm) { nav.popBackStack() }
            }
        }
    }
}
