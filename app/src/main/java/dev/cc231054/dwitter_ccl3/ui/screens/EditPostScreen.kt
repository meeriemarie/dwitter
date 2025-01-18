package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dev.cc231054.dwitter_ccl3.ui.BackButton

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Text(
        modifier = modifier,
        text = "Edit Post Screen"
    )

    BackButton(
        modifier = modifier,
        navController = navController
    )
}