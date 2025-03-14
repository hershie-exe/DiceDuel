package com.example.diceduelv1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    val targetScore = remember { mutableIntStateOf(101) }
    val rerollCount = remember { mutableIntStateOf(0) }
    val selectedDice = remember { mutableStateOf(mutableSetOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE587A4), Color(0xFFF5E6EB), Color(0xFFE7B7D4), Color(0xFFFFD1E3), Color(0xFFF8C8DC)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoreDisplay("COMPUTER", computerScore.value, targetScore.value)
        DiceRow(dice = computerDice.value, isDisabled = true)

        Spacer(modifier = Modifier.height(40.dp))

        DiceRow(dice = humanDice.value, isDisabled = false, selectedDice = selectedDice.value) {
            if (rerollCount.value < 2) {
                if (selectedDice.value.contains(it)) {
                    selectedDice.value.remove(it)
                } else {
                    selectedDice.value.add(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        ScoreDisplay("PLAYER", humanScore.value, targetScore.value)
        Spacer(modifier = Modifier.height(30.dp))

        Row {
            StyledButton("THROW") {
                if (rollCount.value < 3) {
                    humanDice.value = List(5) { if (selectedDice.value.contains(it)) humanDice.value[it] else Random.nextInt(1, 7) }
                    rollCount.value++
                    rerollCount.value++
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            StyledButton("SCORE", enabled = rollCount.value > 0) {
                humanScore.value += humanDice.value.sum()
                computerTurn(computerDice, computerScore)
                rollCount.value = 0
                rerollCount.value = 0
                selectedDice.value.clear()

                if (humanScore.value >= targetScore.value || computerScore.value >= targetScore.value) {
                    gameOver.value = true
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        StyledButton("BACK TO MENU") { onBack() }

        if (gameOver.value) {
            Text(
                text = if (humanScore.value > computerScore.value) "YOU WIN!" else "YOU LOSE!",
                fontSize = 32.sp,
                color = if (humanScore.value > computerScore.value) Color(0xFF8BC34A) else Color(0xFFD32F2F),
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun DiceRow(dice: List<Int>, isDisabled: Boolean, selectedDice: Set<Int> = emptySet(), onSelect: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        dice.forEachIndexed { index, value ->
            DiceImage(value, isDisabled, selectedDice.contains(index)) {
                onSelect(index)
            }
        }
    }
}

@Composable
fun DiceImage(number: Int, isDisabled: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .padding(4.dp)
            .clickable(enabled = !isDisabled, onClick = onClick)
            .background(if (isSelected) Color(0xFFB39DDB) else Color.Transparent)
    ) {
        Text(
            text = number.toString(),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ScoreDisplay(title: String, score: Int, targetScore: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 22.sp, color = Color(0xFFC05A7D), style = TextStyle(letterSpacing = 2.sp))
        Text(text = "SCORE", fontSize = 20.sp, color = Color(0xFFC05A7D), style = TextStyle(letterSpacing = 2.sp))
        Text(text = "$score / $targetScore", fontSize = 24.sp, color = Color(0xFFF7ADC1), style = TextStyle(letterSpacing = 2.sp))
    }
}

@Composable
fun StyledButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
    ) {
        Box {
            Text(text = text, fontSize = 20.sp, color = Color(0xFFC05A7D), modifier = Modifier.offset(2.dp, 2.dp))
            Text(text = text, fontSize = 20.sp, color = Color(0xFFF7ADC1))
        }
    }
}

fun computerTurn(computerDice: MutableState<List<Int>>, computerScore: MutableState<Int>) {
    var rolls = 0
    while (rolls < 3) {
        if (Random.nextBoolean()) {
            computerDice.value = List(5) { Random.nextInt(1, 7) }
        }
        rolls++
    }
    computerScore.value += computerDice.value.sum()
}