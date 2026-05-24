package com.example.classroomassistant.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CountdownCircle(progress: Float, timeLabel: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.size(240.dp)) {
            drawArc(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )
            drawArc(
                color = MaterialTheme.colorScheme.primary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )
        }
        Text(timeLabel, style = MaterialTheme.typography.headlineMedium)
    }
}
