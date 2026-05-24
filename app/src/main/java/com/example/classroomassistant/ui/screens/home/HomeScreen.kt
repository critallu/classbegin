package com.example.classroomassistant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.ui.components.CourseCard
import com.example.classroomassistant.ui.components.EmptyState
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun HomeScreen(vm: HomeViewModel, onGoCountdown: () -> Unit, onCourseClick: (Long) -> Unit) {
    val state by vm.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(
                Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surface)),
                RoundedCornerShape(24.dp)
            ).padding(20.dp)
        ) {
            Text("${state.greeting}，${state.teacherName}", style = MaterialTheme.typography.headlineMedium)
            Text(java.time.LocalDate.now().toString())
        }
        PrimaryButton("开始上课", onGoCountdown)
        if (state.todayCourses.isEmpty()) EmptyState("今天没有课程安排，祝你轻松备课。")
        state.todayCourses.forEachIndexed { idx, c -> CourseCard(c, highlight = idx == 0) { onCourseClick(c.id) } }
        Text("今日重要事项", style = MaterialTheme.typography.titleMedium)
        if (state.todayEvents.isEmpty()) EmptyState("今天暂无重要事项") else state.todayEvents.forEach { Text("• ${it.title}") }
        Spacer(Modifier.height(80.dp))
    }
}
