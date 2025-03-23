package com.example.diceduelv1

import androidx.compose.foundation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun GameScreen(
    onBack: () -> Unit,
    targetScore: Int // ✅ Now passed in from MainActivity or Settings
) {
    val humanWins = remember { mutableIntStateOf(0) }
    val computerWins = remember { mutableIntStateOf(0) }

    val humanDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val computerDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val humanScore = remember { mutableIntStateOf(0) }
    val computerScore = remember { mutableIntStateOf(0) }
    val rollCount = remember { mutableIntStateOf(0) }
    val rerollCount = remember { mutableIntStateOf(0) }
    val gameOver = remember { mutableStateOf(false) }
    val showGameOverPopup = remember { mutableStateOf(false) }
    val isTie = remember { mutableStateOf(false) }
    val selectedDice = remember { mutableStateOf(mutableSetOf<Int>()) }
    val showRerollPopup = remember { mutableStateOf(false) }

    val resetGame = {
        humanDice.value = List(5) { Random.nextInt(1, 7) }
        computerDice.value = List(5) { Random.nextInt(1, 7) }
        humanScore.value = 0
        computerScore.value = 0
        rollCount.value = 0
        rerollCount.value = 0
        selectedDice.value.clear()
        gameOver.value = false
        showGameOverPopup.value = false
        isTie.value = false
    }

    val endTurn = {
        humanScore.value += humanDice.value.sum()
        computerTurn(computerDice, computerScore)
        rollCount.value = 0
        rerollCount.value = 0
        selectedDice.value.clear()

        if (humanScore.value >= targetScore || computerScore.value >= targetScore) {
            gameOver.value = true
            showGameOverPopup.value = true

            when {
                humanScore.value > computerScore.value -> humanWins.value += 1
                computerScore.value > humanScore.value -> computerWins.value += 1
                else -> isTie.value = true
            }
        }
    }

    val throwDice = {
        humanDice.value = List(5) { Random.nextInt(1, 7) }
        rollCount.value++
        if (rollCount.value >= 3) endTurn()
    }

    val rerollDice = {
        humanDice.value = List(5) {
            if (selectedDice.value.contains(it)) humanDice.value[it] else Random.nextInt(1, 7)
        }
        rollCount.value++
        rerollCount.value++

        if (rollCount.value >= 3) endTurn()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                PixelText("PLAYER:${humanWins.value}", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                PixelText("COMPUTER:${computerWins.value}", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PixelText("COMPUTER", fontSize = 24.sp)
            PixelText("SCORE", fontSize = 18.sp)
            PixelText("${computerScore.value}/$targetScore", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(16.dp))

            DiceGrid(dice = computerDice.value, selectedDice = emptySet(), onDiceSelected = {}, isSelectable = false)

            Spacer(modifier = Modifier.weight(1f))

            if (showRerollPopup.value) {
                RerollPopup(
                    rerollCount = rerollCount.value,
                    onConfirm = {
                        rerollDice()
                        showRerollPopup.value = false
                    },
                    onCancel = { showRerollPopup.value = false }
                )
            }

            DiceGrid(
                dice = humanDice.value,
                selectedDice = selectedDice.value,
                onDiceSelected = { index ->
                    if (rerollCount.value < 2 && rollCount.value < 3) {
                        if (selectedDice.value.contains(index))
                            selectedDice.value.remove(index)
                        else
                            selectedDice.value.add(index)
                    }
                },
                isSelectable = rollCount.value < 3 && rerollCount.value < 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            PixelText("PLAYER", fontSize = 24.sp)
            PixelText("SCORE", fontSize = 18.sp)
            PixelText("${humanScore.value}/$targetScore", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PixelButton(
                    "THROW",
                    onClick = {
                        if (rollCount.value == 0) {
                            throwDice()
                        } else if (rollCount.value < 3 && rerollCount.value < 2) {
                            showRerollPopup.value = true
                        }
                    },
                    enabled = rollCount.value < 3
                )

                PixelButton("SCORE", onClick = endTurn, enabled = !gameOver.value && rollCount.value < 3)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PixelButton("BACK TO MENU", onClick = onBack)

            if (showGameOverPopup.value) {
                GameOverPopup(
                    isWin = when {
                        humanScore.value > computerScore.value -> true
                        computerScore.value > humanScore.value -> false
                        else -> null
                    },
                    onReplay = resetGame,
                    onBack = onBack
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
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(12.dp)
            .alpha(if (enabled) 1f else 0.5f)
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

// Extension function to add alpha support for Modifier
fun Modifier.alpha(alpha: Float): Modifier {
    return this.then(
        Modifier.graphicsLayer(alpha = alpha)
    )
}