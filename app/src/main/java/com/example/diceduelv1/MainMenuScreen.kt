package com.example.diceduelv1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val pixelFont = FontFamily(Font(R.font.press_start_2p))

@Composable
fun MainMenuScreen(onNavigate: (String) -> Unit) {
    //Detect screen orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    //Gradient background and centered content box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
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
        if (isLandscape) {
            //Landscape layout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //Large Logo on the left
                val logo: Painter = painterResource(id = R.drawable.diceduel)
                Image(
                    painter = logo,
                    contentDescription = "Dice Duel Logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight(0.9f)
                        .padding(end = 24.dp)
                )

                //Buttons on the right
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(start = 16.dp)
                ) {
                    MenuButton(text = "NEW GAME") { onNavigate("game") }
                    Spacer(modifier = Modifier.height(20.dp))
                    MenuButton(text = "SETTINGS") { onNavigate("settings") }
                    Spacer(modifier = Modifier.height(20.dp))
                    MenuButton(text = "ABOUT") { onNavigate("about") }
                }
            }
        } else {
            //Portrait layout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //Logo on top
                val logo: Painter = painterResource(id = R.drawable.diceduel)
                Image(
                    painter = logo,
                    contentDescription = "Dice Duel Logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(220.dp)
                        .padding(bottom = 60.dp)
                )

                //Menu buttons below
                MenuButton(text = "NEW GAME") { onNavigate("game") }
                Spacer(modifier = Modifier.height(25.dp))
                MenuButton(text = "SETTINGS") { onNavigate("settings") }
                Spacer(modifier = Modifier.height(25.dp))
                MenuButton(text = "ABOUT") { onNavigate("about") }
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    //Custom-styled pixel button with shadow text
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick)
    ) {
        // Shadow/border text
        Text(
            text = text,
            fontSize = 24.sp,
            fontFamily = pixelFont,
            color = Color(0xFFC05A7D),
            style = TextStyle(letterSpacing = 2.sp),
            modifier = Modifier.offset(2.dp, 2.dp)
        )
        // Foreground text
        Text(
            text = text,
            fontSize = 24.sp,
            fontFamily = pixelFont,
            color = Color(0xFFF7ADC1),
            style = TextStyle(letterSpacing = 2.sp),
            modifier = Modifier.offset(-2.dp, -2.dp)
        )
    }
}
