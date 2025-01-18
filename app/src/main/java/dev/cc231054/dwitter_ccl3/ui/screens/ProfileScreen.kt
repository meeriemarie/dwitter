package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.cc231054.dwitter_ccl3.ui.AddPostButton
import dev.cc231054.dwitter_ccl3.ui.PostList
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box (modifier = Modifier.fillMaxSize()) {
        LogoutBtn(modifier = modifier)
        AddPostButton(
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            navController = navController,
        )
    }
}


@Composable
fun LogoutBtn(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
    Column (modifier = modifier) {
        Button(onClick = {viewModel.logout()}) {
            Text(text = "Log Out")
        }
    }
}