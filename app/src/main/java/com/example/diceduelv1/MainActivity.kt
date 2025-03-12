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

// Handles screen navigation
@Composable
fun DiceDuelGame() {
    var screenState by remember { mutableStateOf("menu") }

    when (screenState) {
        "menu" -> MainMenu { screenState = it }
        "game" -> GameScreen { screenState = "menu" }
        "about" -> AboutScreen { screenState = "menu" }
    }
}
