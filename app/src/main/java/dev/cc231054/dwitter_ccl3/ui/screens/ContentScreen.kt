package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cc231054.dwitter_ccl3.ui.AddPostButton
import dev.cc231054.dwitter_ccl3.ui.PostList

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentUserId: String
) {

    Box (modifier = Modifier.fillMaxSize()) {
        PostList(
            modifier = modifier,
            currentUserId = currentUserId
        )
        AddPostButton(
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController,
        )
    }
}