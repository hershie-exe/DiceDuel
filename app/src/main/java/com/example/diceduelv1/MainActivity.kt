package com.example.diceduelv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceDuelGame()
        }
    }
}

@Composable
fun DiceDuelGame() {
    var screenState by remember { mutableStateOf("menu") }

    // 🎯 Target score state shared between screens
    var targetScore by remember { mutableIntStateOf(101) }

    when (screenState) {
        "menu" -> MainMenuScreen { screenState = it }

        "game" -> GameScreen(
            onBack = { screenState = "menu" },
            targetScore = targetScore
        )

        "about" -> AboutScreen { screenState = "menu" }

        "settings" -> SettingsScreen(
            currentTargetScore = targetScore,
            onSaveTarget = { newScore ->
                targetScore = newScore
                screenState = "menu" // Navigate back after saving
            },
            onBack = { screenState = "menu" }
        )
    }
}
