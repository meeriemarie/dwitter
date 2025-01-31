package dev.cc231054.dwitter_ccl3.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBackButton: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = { onBackButton() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = "Cancel", color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}