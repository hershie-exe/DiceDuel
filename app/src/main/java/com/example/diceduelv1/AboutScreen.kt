package com.example.diceduelv1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xAA000000)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .run {
                        if (isLandscape) {
                            fillMaxWidth(0.6f)
                        } else {
                            fillMaxWidth(0.8f)
                        }
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    PixelText("ABOUT", fontSize = 24.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    PixelText(
                        "Author: Your Name (Student ID)",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PixelText(
                        "I confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE587A4))
                            .clickable(onClick = onBack)
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PixelText("OK", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

git commit -m "made change in about screen"