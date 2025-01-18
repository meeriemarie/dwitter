package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        LogoutBtn()
        AddPostButton(navController = navController)
    }
}


@Composable
fun LogoutBtn(viewModel: UserViewModel = viewModel()
) {
    Column {
        Button(onClick = {viewModel.logout()}) {
            Text(text = "Log Out")
        }
    }
}