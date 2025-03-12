package com.example.diceduelv1

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun GameScreen(onBack: () -> Unit) {
    var humanDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val computerDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val humanScore = remember { mutableIntStateOf(0) }
    val computerScore = remember { mutableIntStateOf(0) }
    var rollCount by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    val targetScore = remember { mutableIntStateOf(101) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("H: ${humanScore.value} / C: ${computerScore.value}", fontSize = 20.sp)
            if (gameOver) {
                Text(
                    text = if (humanScore.value > computerScore.value) "You Win!" else "You Lose!",
                    fontSize = 24.sp,
                    color = if (humanScore.value > computerScore.value) Color.Green else Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        DiceRow("You", humanDice)
        DiceRow("Computer", computerDice.value)

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Button(
                onClick = {
                    if (rollCount < 3) {
                        humanDice = List(5) { Random.nextInt(1, 7) }
                        rollCount++
                    }
                },
                enabled = rollCount < 3
            ) {
                Text("Throw")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    humanScore.value += humanDice.sum()
                    computerTurn(computerDice, computerScore)
                    rollCount = 0

                    if (humanScore.value >= targetScore.value || computerScore.value >= targetScore.value) {
                        gameOver = true
                    }
                },
                enabled = rollCount > 0
            ) {
                Text("Score")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onBack) {
            Text("Back to Menu")
        }
    }
}

fun computerTurn(computerDice: MutableState<List<Int>>, computerScore: MutableState<Int>) {
    var rolls = 0
    while (rolls < 3) {
        if (Random.nextBoolean()) { // Randomly decides to reroll
            computerDice.value = List(5) { Random.nextInt(1, 7) }
        }
        rolls++
    }
    computerScore.value += computerDice.value.sum()
}
