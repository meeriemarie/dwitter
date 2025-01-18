package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.cc231054.dwitter_ccl3.ui.PostList

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    currentUserId: String
) {

    Box (modifier = Modifier.fillMaxSize()) {
        PostList(
            modifier = modifier,
            currentUserId = currentUserId
        )
    }
}