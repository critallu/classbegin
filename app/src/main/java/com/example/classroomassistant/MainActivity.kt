package com.example.classroomassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.classroomassistant.ui.navigation.AppNavGraph
import com.example.classroomassistant.ui.theme.ClassroomAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as ClassroomAssistantApp
        setContent {
            ClassroomAssistantTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(app.appContainer)
                }
            }
        }
    }
}
