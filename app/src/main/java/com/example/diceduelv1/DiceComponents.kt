package com.example.diceduelv1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Displays dice row for human or computer
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

