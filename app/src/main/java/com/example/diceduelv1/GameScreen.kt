package com.example.diceduelv1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random


@Composable
fun GameScreen(onBack: () -> Unit) {
    val humanDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val computerDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val humanScore = remember { mutableIntStateOf(0) }
    val computerScore = remember { mutableIntStateOf(0) }
    val rollCount = remember { mutableIntStateOf(0) }
    val gameOver = remember { mutableStateOf(false) }
    val targetScore = 101
    val rerollCount = remember { mutableIntStateOf(0) }
    val selectedDice = remember { mutableStateOf(mutableSetOf<Int>()) }

    // Pink gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE587A4), Color(0xFFF5E6EB), Color(0xFFE7B7D4),
                        Color(0xFFFFD1E3), Color(0xFFF8C8DC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Computer Score Display
            PixelText("COMPUTER", fontSize = 24.sp)
            PixelText("SCORE", fontSize = 18.sp)
            PixelText("${computerScore.value}/$targetScore", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Computer Dice
            DiceGrid(
                dice = computerDice.value,
                selectedDice = emptySet(),
                onDiceSelected = { },
                isSelectable = false
            )

            Spacer(modifier = Modifier.weight(1f))

            // Player Dice
            DiceGrid(
                dice = humanDice.value,
                selectedDice = selectedDice.value,
                onDiceSelected = { index ->
                    if (rerollCount.value < 2) {
                        if (selectedDice.value.contains(index)) {
                            selectedDice.value.remove(index)
                        } else {
                            selectedDice.value.add(index)
                        }
                    }
                },
                isSelectable = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player Score Display
            PixelText("PLAYER", fontSize = 24.sp)
            PixelText("SCORE", fontSize = 18.sp)
            PixelText("${humanScore.value}/$targetScore", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Game Controls - THROW and SCORE buttons side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PixelButton(
                    text = "THROW",
                    onClick = {
                        if (rollCount.value < 3) {
                            humanDice.value = List(5) {
                                if (selectedDice.value.contains(it)) humanDice.value[it]
                                else Random.nextInt(1, 7)
                            }
                            rollCount.value++
                            rerollCount.value++
                        }
                    }
                )

                PixelButton(
                    text = "SCORE",
                    onClick = {
                        humanScore.value += humanDice.value.sum()
                        computerTurn(computerDice, computerScore)
                        rollCount.value = 0
                        rerollCount.value = 0
                        selectedDice.value.clear()

                        if (humanScore.value >= targetScore || computerScore.value >= targetScore) {
                            gameOver.value = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            PixelButton(text = "BACK TO MENU", onClick = onBack)

            if (gameOver.value) {
                Text(
                    text = if (humanScore.value > computerScore.value) "YOU WIN!" else "YOU LOSE!",
                    fontSize = 32.sp,
                    color = if (humanScore.value > computerScore.value) Color(0xFF8BC34A) else Color(0xFFD32F2F),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// Dice Grid Layout (3 Dice on Top, 2 Dice Below)
@Composable
fun DiceGrid(
    dice: List<Int>,
    selectedDice: Set<Int>,
    onDiceSelected: (Int) -> Unit,
    isSelectable: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in 0 until 3) {
                DiceImage(
                    number = dice[i],
                    isSelected = selectedDice.contains(i),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(i) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in 3 until 5) {
                DiceImage(
                    number = dice[i],
                    isSelected = selectedDice.contains(i),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(i) }
                )
            }
        }
    }
}

// Dice Image Component
@Composable
fun DiceImage(
    number: Int,
    isSelected: Boolean,
    isSelectable: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (isSelectable)
                    Modifier.clickable(onClick = onClick)
                else
                    Modifier
            )
    ) {
        Image(
            painter = painterResource(id = getDiceImage(number)),
            contentDescription = "Dice $number",
            modifier = Modifier
                .size(70.dp)
                .padding(4.dp)
                .then(
                    if (isSelected)
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFD84D88).copy(alpha = 0.3f))
                    else
                        Modifier
                )
        )
    }
}

// Returns the correct drawable for a dice value
fun getDiceImage(number: Int): Int {
    return when (number) {
        1 -> R.drawable.die1
        2 -> R.drawable.die2
        3 -> R.drawable.die3
        4 -> R.drawable.die4
        5 -> R.drawable.die5
        6 -> R.drawable.die6
        else -> R.drawable.die1
    }
}

// Pixel-Style Text (Dark Pink Border + Light Pink Inner)
@Composable
fun PixelText(text: String, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box {
        // Outer Border
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = pixelFont,
            color = Color(0xFFC05A7D),
            style = TextStyle(letterSpacing = 2.sp),
            modifier = Modifier.offset(2.dp, 2.dp)
        )
        // Inner Text
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = pixelFont,
            color = Color(0xFFF7ADC1),
            style = TextStyle(letterSpacing = 2.sp)
        )
    }
}

// Pixel-Style Button
@Composable
fun PixelButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        PixelText(text, fontSize = 20.sp)
    }
}

// Computer AI function
fun computerTurn(computerDice: MutableState<List<Int>>, computerScore: MutableState<Int>) {
    // Simple AI strategy: roll up to 3 times, keeping track of the best rolls
    var bestRoll = computerDice.value
    var bestScore = bestRoll.sum()

    repeat(2) { // Computer will try up to 2 rerolls
        val newRoll = List(5) { Random.nextInt(1, 7) }
        val newScore = newRoll.sum()

        if (newScore > bestScore) {
            bestRoll = newRoll
            bestScore = newScore
        }
    }

    computerDice.value = bestRoll
    computerScore.value += bestScore
}