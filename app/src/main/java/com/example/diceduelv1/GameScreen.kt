package com.example.diceduelv1

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.activity.compose.BackHandler

@Composable
fun GameScreen(
    onBack: () -> Unit,
    targetScore: Int
) {
    // Using rememberSaveable instead of ViewModel to persist state across configuration changes
    val humanWins = rememberSaveable { mutableIntStateOf(0) }
    val computerWins = rememberSaveable { mutableIntStateOf(0) }
    val humanDice = rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val computerDice = rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val humanScore = rememberSaveable { mutableIntStateOf(0) }
    val computerScore = rememberSaveable { mutableIntStateOf(0) }
    val rollCount = rememberSaveable { mutableIntStateOf(0) }
    val rerollCount = rememberSaveable { mutableIntStateOf(0) }
    val gameOver = rememberSaveable { mutableStateOf(false) }
    val showGameOverPopup = rememberSaveable { mutableStateOf(false) }
    val isTie = rememberSaveable { mutableStateOf(false) }
    val selectedDice = rememberSaveable { mutableStateOf(mutableSetOf<Int>()) }
    val showRerollPopup = rememberSaveable { mutableStateOf(false) }

    // Fix: Use 'by' delegate instead of '.value' to properly observe state changes
    var isInGame by rememberSaveable { mutableStateOf(true) }

    // Check if we're in landscape mode
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Intercept back button to use our custom back navigation
    BackHandler(enabled = isInGame) {
        // Handle manual back press same as pressing the back button
        if (isInGame) {
            // Ask for confirmation before going back to menu
            // For simplicity, we'll just go back directly here
            isInGame = false
            onBack()
        }
    }

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

    // Custom back action that preserves state
    val safeBack = {
        isInGame = false
        onBack()
    }

    // Let's ensure the game remains in the game screen during orientation changes
    LaunchedEffect(Unit) {
        isInGame = true
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
        if (isLandscape) {
            // Landscape layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side - Computer
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PixelText("COMPUTER", fontSize = 24.sp)
                    PixelText("SCORE", fontSize = 18.sp)
                    PixelText("${computerScore.value}/$targetScore", fontSize = 22.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    DiceGrid(
                        dice = computerDice.value,
                        selectedDice = emptySet(),
                        onDiceSelected = {},
                        isSelectable = false,
                        isLandscape = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        PixelText("COMPUTER:", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        PixelText("${computerWins.value}", fontSize = 18.sp)
                    }
                }

                // Middle - Controls
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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

                    Spacer(modifier = Modifier.height(16.dp))

                    PixelButton(
                        "SCORE",
                        onClick = endTurn,
                        enabled = !gameOver.value && rollCount.value < 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Use safe back instead of direct onBack
                    PixelButton("BACK TO MENU", onClick = safeBack)
                }

                // Right side - Player
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PixelText("PLAYER", fontSize = 24.sp)
                    PixelText("SCORE", fontSize = 18.sp)
                    PixelText("${humanScore.value}/$targetScore", fontSize = 22.sp)

                    Spacer(modifier = Modifier.height(16.dp))

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
                        isSelectable = rollCount.value < 3 && rerollCount.value < 2,
                        isLandscape = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        PixelText("PLAYER:", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        PixelText("${humanWins.value}", fontSize = 18.sp)
                    }
                }
            }
        } else {
            // Portrait layout (original layout)
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

                DiceGrid(
                    dice = computerDice.value,
                    selectedDice = emptySet(),
                    onDiceSelected = {},
                    isSelectable = false,
                    isLandscape = false
                )

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
                    isSelectable = rollCount.value < 3 && rerollCount.value < 2,
                    isLandscape = false
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

                // Use safe back instead of direct onBack
                PixelButton("BACK TO MENU", onClick = safeBack)
            }
        }

        if (showGameOverPopup.value) {
            GameOverPopup(
                isWin = when {
                    humanScore.value > computerScore.value -> true
                    computerScore.value > humanScore.value -> false
                    else -> null
                },
                onReplay = resetGame,
                onBack = safeBack // Use the safe back here too
            )
        }
    }
}

// Updated Dice Grid Layout with landscape support
@Composable
fun DiceGrid(
    dice: List<Int>,
    selectedDice: Set<Int>,
    onDiceSelected: (Int) -> Unit,
    isSelectable: Boolean,
    isLandscape: Boolean
) {
    if (isLandscape) {
        // In landscape, arrange 3x2 grid (3 rows, 2 dice per row)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DiceImage(
                    number = dice[0],
                    isSelected = selectedDice.contains(0),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(0) },
                    size = 60.dp
                )
                DiceImage(
                    number = dice[1],
                    isSelected = selectedDice.contains(1),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(1) },
                    size = 60.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DiceImage(
                    number = dice[2],
                    isSelected = selectedDice.contains(2),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(2) },
                    size = 60.dp
                )
                DiceImage(
                    number = dice[3],
                    isSelected = selectedDice.contains(3),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(3) },
                    size = 60.dp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                DiceImage(
                    number = dice[4],
                    isSelected = selectedDice.contains(4),
                    isSelectable = isSelectable,
                    onClick = { onDiceSelected(4) },
                    size = 60.dp
                )
            }
        }
    } else {
        // Original portrait layout (3 dice on top, 2 below)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 3) {
                    DiceImage(
                        number = dice[i],
                        isSelected = selectedDice.contains(i),
                        isSelectable = isSelectable,
                        onClick = { onDiceSelected(i) },
                        size = 70.dp
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
                        onClick = { onDiceSelected(i) },
                        size = 70.dp
                    )
                }
            }
        }
    }
}

// Updated Dice Image Component with configurable size
@Composable
fun DiceImage(
    number: Int,
    isSelected: Boolean,
    isSelectable: Boolean,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp
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
                .size(size)
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