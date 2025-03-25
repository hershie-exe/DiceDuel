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

    // Display a full-screen dialog that allows dismissal
    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        // Semi-transparent background
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xAA000000)),
            contentAlignment = Alignment.Center
        ) {
            // Main card box with gradient background
            Box(
                modifier = Modifier
                    .run {
                        if (isLandscape) {
                            fillMaxWidth(0.6f) // Narrower width in landscape
                        } else {
                            fillMaxWidth(0.8f) // Wider in portrait
                        }
                    }
                    .clip(RoundedCornerShape(12.dp)) // Rounded corners
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
                // Content column inside the dialog
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Title
                    PixelText("ABOUT", fontSize = 24.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author info
                    PixelText(
                        "Author: Hirushi Jayasekara (IIT ID:20232507,UOW ID: w2087743)",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Plagiarism statement
                    PixelText(
                        "I confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // OK Button to dismiss
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
