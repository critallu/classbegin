package com.example.classroomassistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(text, modifier = Modifier.padding(20.dp))
    }
}
