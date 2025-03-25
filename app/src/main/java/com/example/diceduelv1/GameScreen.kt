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
    // State management for game logic and UI
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

    // Tracks whether user is inside the game screen
    var isInGame by rememberSaveable { mutableStateOf(true) }

    // Intercept physical back button
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Intercept physical back button
    BackHandler(enabled = isInGame) {

        if (isInGame) {

            isInGame = false
            onBack()
        }
    }

    // Reset game to initial state
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

    // End human turn and start computer turn, check win conditions
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

    // First dice roll by the player
    val throwDice = {
        humanDice.value = List(5) { Random.nextInt(1, 7) }
        rollCount.value++
        if (rollCount.value >= 3) endTurn()
    }

    // Reroll logic for selected dice
    val rerollDice = {
        humanDice.value = List(5) {
            if (selectedDice.value.contains(it)) humanDice.value[it] else Random.nextInt(1, 7)
        }
        rollCount.value++
        rerollCount.value++

        if (rollCount.value >= 3) endTurn()
    }

    // Safe back navigation handler
    val safeBack = {
        isInGame = false
        onBack()
    }

    // Ensure game stays on this screen even after orientation change
    LaunchedEffect(Unit) {
        isInGame = true
    }

    // UI layout container
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
            // Landscape layout: 3-column layout (Computer - Controls - Player)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Left: Computer player section
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

                // Center: Game control buttons
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Throw button: Rolls all dice if it's the first roll,
                    // else opens reroll popup if user still has rerolls left
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
                    // Score button: Ends human turn and triggers computer turn
                    PixelButton(
                        "SCORE",
                        onClick = endTurn,
                        enabled = !gameOver.value && rollCount.value < 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Menu: Center-aligned button inside a Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PixelButton("BACK TO MENU", onClick = safeBack)
                    }
                }

                // Right: Player section in landscape
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

                    // Conditional reroll popup
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

                    // Player dice grid with reroll support
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

                    // Player win count
                    Row {
                        PixelText("PLAYER:", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        PixelText("${humanWins.value}", fontSize = 18.sp)
                    }
                }
            }
        } else {

            // Portrait layout (single column)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top row showing win counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    PixelText("PLAYER:${humanWins.value}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    PixelText("COMPUTER:${computerWins.value}", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Computer header and score
                PixelText("COMPUTER", fontSize = 24.sp)
                PixelText("SCORE", fontSize = 18.sp)
                PixelText("${computerScore.value}/$targetScore", fontSize = 22.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Computer dice
                DiceGrid(
                    dice = computerDice.value,
                    selectedDice = emptySet(),
                    onDiceSelected = {},
                    isSelectable = false,
                    isLandscape = false
                )

                Spacer(modifier = Modifier.weight(1f))

                // Reroll popup in portrait mode
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

                // Player dice and selection
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

                // Player header and score
                PixelText("PLAYER", fontSize = 24.sp)
                PixelText("SCORE", fontSize = 18.sp)
                PixelText("${humanScore.value}/$targetScore", fontSize = 22.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // Throw and Score buttons in portrait
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

                // Back to Menu in portrait mode
                PixelButton("BACK TO MENU", onClick = safeBack)
            }
        }

        // Final Game Over popup (shown after win condition)
        if (showGameOverPopup.value) {
            GameOverPopup(
                isWin = when {
                    humanScore.value > computerScore.value -> true
                    computerScore.value > humanScore.value -> false
                    else -> null
                },
                onReplay = resetGame,
                onBack = safeBack
            )
        }
    }
}


@Composable
fun DiceGrid(
    dice: List<Int>, // List of dice values (1–6)
    selectedDice: Set<Int>,  // Indices of dice selected by human player
    onDiceSelected: (Int) -> Unit,
    isSelectable: Boolean,  // Whether the user can currently select dice
    isLandscape: Boolean   // Layout mode: true = landscape, false = portrait
) {
    if (isLandscape) {

        // Landscape layout: 3 rows (2 dice + 2 dice + 1 dice)
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
        // Portrait layout: 2 rows (3 dice + 2 dice)
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


@Composable
fun DiceImage(
    number: Int,  // Dice face value (1 to 6)
    isSelected: Boolean, // Whether this dice is selected by the player
    isSelectable: Boolean, // Whether dice selection is currently allowed
    onClick: () -> Unit,  // Callback when dice is clicked
    size: androidx.compose.ui.unit.Dp    // Dice size (varies for landscape/portrait)
) {
    Box(
        modifier = Modifier
            // Make dice clickable only if selectable
            .then(
                if (isSelectable)
                    Modifier.clickable(onClick = onClick)
                else
                    Modifier
            )
    ) {
        Image(
            painter = painterResource(id = getDiceImage(number)),  // Load dice image based on value
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

// Returns the correct dice image resource based on dice number
fun getDiceImage(number: Int): Int {
    return when (number) {
        1 -> R.drawable.die1
        2 -> R.drawable.die2
        3 -> R.drawable.die3
        4 -> R.drawable.die4
        5 -> R.drawable.die5
        6 -> R.drawable.die6
        else -> R.drawable.die1  // Fallback to die1 if out of range
    }
}


@Composable
fun PixelText(text: String, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box {
        // Bottom layer: dark pink for border/shadow effect
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = pixelFont,
            color = Color(0xFFC05A7D),
            style = TextStyle(letterSpacing = 2.sp),
            modifier = Modifier.offset(2.dp, 2.dp)
        )

        // Top layer: light pink foreground text
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = pixelFont,
            color = Color(0xFFF7ADC1),
            style = TextStyle(letterSpacing = 2.sp)
        )
    }
}


@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)  // Enable/disable click based on state
            .padding(12.dp)
            .alpha(if (enabled) 1f else 0.5f)  // Visually fade when disabled
    ) {
        PixelText(text, fontSize = 20.sp) // Use pixelated text inside button
    }
}

// Computer AI function
fun computerTurn(computerDice: MutableState<List<Int>>, computerScore: MutableState<Int>) {
    var currentDice = List(5) { Random.nextInt(1, 7) } // First roll
    val diceToKeep = MutableList(5) { false } // Track which dice to keep

    val rerollAttempts = Random.nextInt(0, 3)  // Random number of rerolls (0–2)

    repeat(rerollAttempts) {
        for (i in 0 until 5) {

            diceToKeep[i] = Random.nextBoolean()
        }

        // Reroll only unkept dice
        currentDice = currentDice.mapIndexed { index, value ->
            if (diceToKeep[index]) value else Random.nextInt(1, 7)  // Randomly decide which dice to keep
        }
    }

    computerDice.value = currentDice // Update UI with final dice result
    computerScore.value += currentDice.sum() // Add to total computer score
}

// Modifier extension to apply alpha (transparency)
fun Modifier.alpha(alpha: Float): Modifier {
    return this.then(
        Modifier.graphicsLayer(alpha = alpha)
    )
}



// --------------------- COMPUTER AI STRATEGY DOCUMENTATION ---------------------
/*
    STRATEGY OVERVIEW:
    This AI follows a randomized strategy for its turn.
    - It starts with an initial roll of 5 dice.
    - Then, it performs 0 to 2 rerolls — the number is chosen randomly.
    - In each reroll, the AI randomly decides which dice to keep (via `Random.nextBoolean()`).
    - Only the unkept dice are rerolled in the next round.
    - After the final roll, the sum of the dice is added to the computer’s total score.

    JUSTIFICATION:
    - This approach introduces unpredictability, simulating a human player's uncertain decisions.
    - It satisfies the coursework requirement that the AI must **not always use all 3 rolls**, and that dice retention decisions must be **random**.
    - It's fair and simple, but still allows for decent variation in game outcomes.

    ADVANTAGES:
    - Meets the exact coursework specification for a randomized reroll strategy.
    - Produces natural-looking, non-deterministic outcomes — feels realistic.
    - Keeps the game challenging and balanced, without being unbeatable.

    DISADVANTAGES:
    - Not optimized for maximum scoring (e.g., doesn't keep high-value dice on purpose).
    - May sometimes make "bad" decisions (e.g., keeping low dice like 1 or 2 randomly).
    - Doesn’t adapt based on human player's score or game state — no strategic awareness.

    Overall, this implementation is simple, effective, and aligned with assignment rules.
*/
//