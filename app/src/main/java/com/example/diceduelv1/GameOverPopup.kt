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
    isWin: Boolean,
    onReplay: () -> Unit,
    onBack: () -> Unit
) {
    // Create the transition state outside of the remember block
    val visibleState = remember { MutableTransitionState(false) }

    // Use LaunchedEffect separately
    LaunchedEffect(Unit) {
        delay(200)
        visibleState.targetState = true
    }

    // Use Dialog for better positioning and system behavior
    Dialog(
        onDismissRequest = { /* No-op to prevent dismissal on outside click */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000)), // Semi-transparent overlay
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(animationSpec = tween(500)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(500)) +
                        expandVertically(expandFrom = Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isWin) Color(0xFF8DC891) else Color(0xFFE587A4), // Green for win, pink for lose
                                    if (isWin) Color(0xFFD1F0D3) else Color(0xFFF5E6EB),
                                    if (isWin) Color(0xFF8CD9A0) else Color(0xFFE7B7D4),
                                    if (isWin) Color(0xFFE8F8E9) else Color(0xFFFFD1E3),
                                    if (isWin) Color(0xFFB8E8C5) else Color(0xFFF8C8DC)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // "You Win" or "You Lose" Image
                        Image(
                            painter = painterResource(if (isWin) R.drawable.youwin else R.drawable.youlose),
                            contentDescription = if (isWin) "You Win" else "You Lose",
                            modifier = Modifier.size(150.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Replay and Back to Menu Buttons
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