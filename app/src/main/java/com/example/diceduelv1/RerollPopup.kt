package com.example.diceduelv1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun RerollPopup(
    onConfirm: () -> Unit, // Triggered when user chooses to reroll
    onCancel: () -> Unit,  // Triggered when user cancels reroll
    rerollCount: Int       // Current number of reroll attempts used
) {
    // Detect device orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Full-screen dialog with no dismissal by outside tap or back press
    Dialog(
        onDismissRequest = { /* No-op to prevent dismissal */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        // Semi-transparent dark overlay background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000)),
            contentAlignment = Alignment.Center
        ) {
            // Popup container box with responsive width
            Box(
                modifier = Modifier
                    .run {
                        if (isLandscape) fillMaxWidth(0.6f)
                        else fillMaxWidth(0.8f)
                    }
                    .clip(RoundedCornerShape(12.dp))
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
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Popup content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Reroll text and current attempt info
                    PixelText("REROLL?", fontSize = 24.sp)
                    PixelText("ATTEMPT", fontSize = 20.sp)
                    PixelText("$rerollCount/2", fontSize = 22.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // YES / NO options with dice icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // YES column
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PixelText("YES", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.die3),
                                contentDescription = "Yes Dice",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable(onClick = onConfirm)
                            )
                        }

                        // NO column
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PixelText("NO", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.die6),
                                contentDescription = "No Dice",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable(onClick = onCancel)
                            )
                        }
                    }
                }
            }
        }
    }
}
