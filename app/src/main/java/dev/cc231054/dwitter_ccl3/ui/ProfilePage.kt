package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.UserEntity

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    ProfileBanner()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
    }
}

@Composable
fun ProfileBanner(viewModel: UserViewModel = viewModel()) {
    val profileData by viewModel.userProfile.collectAsState()
    Row(Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalArrangement = Arrangement.Start
        ) {
        LazyColumn {
            items(profileData) {
                user: UserEntity ->
                Spacer(Modifier.padding(8.dp))
                Text(text = user.username)
                Text(text = "@${user.name}")
            }
        }
        Button(
            onClick = {}
        ) {
            Text(text = "Edit Profile")
            Icon( imageVector = Icons.Filled.Edit, contentDescription = "Edit")
        }
    }
}


@Composable
fun LogoutBtn(viewModel: UserViewModel = viewModel()
) {
    Button(onClick = {viewModel.logout()}) {
        Text(text = "Log Out")
    }
}