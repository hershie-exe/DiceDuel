package com.example.diceduelv1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun GameOverPopup(
    isWin: Boolean?, // Nullable for tie
    onReplay: () -> Unit,
    onBack: () -> Unit
) {
    val visibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visibleState.targetState = true
    }

    // Gradient background based on result
    val gradientColors = when (isWin) {
        true -> listOf(
            Color(0xFF8DC891), Color(0xFFD1F0D3),
            Color(0xFF8CD9A0), Color(0xFFE8F8E9), Color(0xFFB8E8C5)
        )
        false -> listOf(
            Color(0xFFE587A4), Color(0xFFF5E6EB),
            Color(0xFFE7B7D4), Color(0xFFFFD1E3), Color(0xFFF8C8DC)
        )
        null -> listOf(
            Color(0xFFB39DDB), Color(0xFFD1C4E9),
            Color(0xFFEDE7F6), Color(0xFFF3E5F5), Color(0xFFE1BEE7)
        )
    }

    val outcomeImage = when (isWin) {
        true -> R.drawable.youwin
        false -> R.drawable.youlose
        null -> R.drawable.tie // make sure tie.png or tie drawable exists
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(500)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(500)) +
                        expandVertically(expandFrom = Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // 🎯 Main Image (larger size, centered)
                        Image(
                            painter = painterResource(id = outcomeImage),
                            contentDescription = "Outcome Image",
                            modifier = Modifier.size(220.dp) // ⬅️ Increased size for emphasis
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🎮 Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PixelText("REPLAY", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.die1),
                                    contentDescription = "Replay",
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clickable(onClick = onReplay)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PixelText("MENU", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.die2),
                                    contentDescription = "Menu",
                                    modifier = Modifier
                                        .size(75.dp)
                                        .clickable(onClick = onBack)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
