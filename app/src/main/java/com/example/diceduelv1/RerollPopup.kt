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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RerollPopup(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    rerollCount: Int
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
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
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                PixelText("REROLL?", fontSize = 28.sp)
                PixelText("ATTEMPT", fontSize = 22.sp)
                PixelText("$rerollCount/2", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelText("YES", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(painter = painterResource(id = R.drawable.die3), contentDescription = "Yes Dice", modifier = Modifier.size(75.dp).clickable(onClick = onConfirm))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelText("NO", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(painter = painterResource(id = R.drawable.die6), contentDescription = "No Dice", modifier = Modifier.size(75.dp).clickable(onClick = onCancel))
                    }
                }
            }
        }
    }
}
