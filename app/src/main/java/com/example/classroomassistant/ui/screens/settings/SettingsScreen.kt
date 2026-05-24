package com.example.classroomassistant.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classroomassistant.ui.components.PrimaryButton

@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val settings by vm.settings.collectAsState()
    var showClearConfirm by remember { mutableStateOf(false) }
    var name by remember(settings.teacherName) { mutableStateOf(settings.teacherName) }
    var duration by remember(settings.defaultDurationMinutes) { mutableStateOf(settings.defaultDurationMinutes.toString()) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("设置")
        OutlinedTextField(name, { name = it }, label = { Text("教师姓名") })
        OutlinedTextField(duration, { duration = it }, label = { Text("默认课时") })
        androidx.compose.foundation.layout.Row { Text("震动提醒"); Switch(settings.vibrationEnabled, { vm.save(settings.copy(vibrationEnabled = it)) }) }
        androidx.compose.foundation.layout.Row { Text("深色模式(预留)"); Switch(settings.darkModeEnabled, { vm.save(settings.copy(darkModeEnabled = it)) }) }
        PrimaryButton("保存设置") { vm.save(settings.copy(teacherName = name, defaultDurationMinutes = duration.toIntOrNull() ?: 40)) }
        PrimaryButton("清空本地数据") { showClearConfirm = true }
    }
    if (showClearConfirm) {
        AlertDialog(onDismissRequest = { showClearConfirm = false },
            confirmButton = { TextButton(onClick = { vm.clearAll(); showClearConfirm = false }) { Text("确认清空") } },
            dismissButton = { TextButton(onClick = { showClearConfirm = false }) { Text("取消") } },
            title = { Text("危险操作") },
            text = { Text("将删除课程与事项，是否继续？") })
    }
}
