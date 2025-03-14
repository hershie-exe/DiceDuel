package com.example.diceduelv1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun DiceRow(dice: List<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        dice.forEach { diceValue ->
            DiceImage(diceValue)
        }
    }
}

// Dice Image Component (Properly Sized & Centered)
@Composable
fun DiceImage(number: Int) {
    Image(
        painter = painterResource(id = getDiceImage(number)),
        contentDescription = "Dice $number",
        modifier = Modifier
            .size(80.dp) // ✅ Increased size for better visibility
            .padding(6.dp)
    )
}

// Get Dice Image Resource
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
