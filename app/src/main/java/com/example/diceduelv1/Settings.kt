package com.example.diceduelv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    currentTargetScore: Int,
    onBack: () -> Unit,
    onSaveTarget: (Int) -> Unit
) {
    var targetScore by remember { mutableIntStateOf(currentTargetScore) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE587A4),
                        Color(0xFFF5E6EB),
                        Color(0xFFE7B7D4),
                        Color(0xFFFFD1E3),
                        Color(0xFFF8C8DC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixelText("SET TARGET SCORE", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))

            PixelText("TARGET: $targetScore", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = targetScore.toFloat(),
                onValueChange = { targetScore = it.toInt() },
                valueRange = 50f..200f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFC05A7D),
                    activeTrackColor = Color(0xFFF7ADC1)
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                PixelButton("SAVE", onClick = {
                    onSaveTarget(targetScore)
                    onBack()
                })

                PixelButton("BACK", onClick = {
                    onBack()
                })
            }
        }
    }
}
