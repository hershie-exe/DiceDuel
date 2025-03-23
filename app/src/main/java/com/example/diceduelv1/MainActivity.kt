package com.example.diceduelv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.saveable.rememberSaveable
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
    // Use rememberSaveable instead of remember to persist state across configuration changes
    var screenState by rememberSaveable { mutableStateOf("menu") }

    // Also use rememberSaveable for targetScore
    var targetScore by rememberSaveable { mutableIntStateOf(101) }

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