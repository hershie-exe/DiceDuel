package com.example.diceduelv1

import androidx.compose.material.*
import androidx.compose.runtime.Composable

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
