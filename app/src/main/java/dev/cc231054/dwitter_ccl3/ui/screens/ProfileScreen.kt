package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
) {
    Box (modifier = Modifier.fillMaxSize()) {
        LogoutBtn(modifier = modifier)
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