package com.example.diceduelv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceDuelGame()
        }
    }
}

// Manages the screen navigation
@Composable
fun DiceDuelGame() {
    var screenState by remember { mutableStateOf("menu") }

    when (screenState) {
        "menu" -> MainMenu { screenState = it }
        "game" -> GameScreen { screenState = "menu" }
        "about" -> AboutScreen { screenState = "menu" }
    }
}

// Main Menu Screen
@Composable
fun MainMenu(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dice Duel v1", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onNavigate("game") }) {
            Text("New Game")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { onNavigate("about") }) {
            Text("About")
        }
    }
}

// About Screen
@Composable
fun AboutScreen(onBack: () -> Unit) {
    AlertDialog(
        onDismissRequest = onBack,
        title = { Text("About") },
        text = {
            Text(
                "Author: Your Name (Student ID)\n\n" +
                        "I confirm that I understand what plagiarism is and have read and understood " +
                        "the section on Assessment Offences. This work is entirely my own."
            )
        },
        confirmButton = {
            Button(onClick = onBack) {
                Text("OK")
            }
        }
    )
}

// Game Screen
@Composable
fun GameScreen(onBack: () -> Unit) {
    var humanDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val computerDice = remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    val humanScore = remember { mutableIntStateOf(0) }
    val computerScore = remember { mutableIntStateOf(0) }
    var rollCount by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("H: ${humanScore.value} / C: ${computerScore.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (gameOver) {
                Text(
                    text = if (humanScore.value > computerScore.value) "You Win!" else "You Lose!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
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

                    if (humanScore.value >= 101 || computerScore.value >= 101) {
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

// Computer AI - Simulates the computer's turn
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

// Displays dice values for both players
@Composable
fun DiceRow(player: String, dice: List<Int>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$player's Dice", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row {
            dice.forEach {
                DiceImage(it)
            }
        }
    }
}

// Displays individual dice images
@Composable
fun DiceImage(number: Int) {
    Image(
        painter = painterResource(id = getDiceImage(number)),
        contentDescription = "Dice $number",
        modifier = Modifier.size(60.dp).padding(4.dp)
    )
}

// Maps dice values to drawable images
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
