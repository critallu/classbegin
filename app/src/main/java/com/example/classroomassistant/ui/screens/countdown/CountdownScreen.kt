package com.example.classroomassistant.ui.screens.countdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.classroomassistant.ui.components.CountdownCircle
import com.example.classroomassistant.ui.components.ReminderChip

@Composable
fun CountdownScreen(vm: CountdownViewModel, onFinish: () -> Unit) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.remainSec, state.totalSec) {
        if (state.remainSec <= 0 && state.totalSec > 0) {
            delay(500)
            onFinish()
        }
    }

    val progress = if (state.totalSec == 0) 0f else state.remainSec.toFloat() / state.totalSec
    val min = state.remainSec / 60
    val sec = state.remainSec % 60
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(state.course?.name ?: "未开始课程")
        Text("${state.course?.className ?: ""} · ${state.course?.classroom ?: ""}")
        CountdownCircle(progress, "%02d:%02d".format(min, sec))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.rules.isEmpty()) ReminderChip("暂无提醒") else state.rules.forEach { ReminderChip("${it.triggerAfterMinutes}分钟: ${it.label}") }
        }
        Text(state.tip)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.pause() }) { Text("暂停") }
            Button(onClick = { vm.resume() }) { Text("继续") }
            Button(onClick = { vm.end(); onFinish() }) { Text("结束") }
        }
    }
}
