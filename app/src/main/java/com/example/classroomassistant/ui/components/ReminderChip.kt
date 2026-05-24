package com.example.classroomassistant.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ReminderChip(label: String) {
    AssistChip(onClick = {}, label = { Text(label) })
}
