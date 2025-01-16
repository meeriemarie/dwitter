package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    UserList(modifier = modifier)
}

@Composable
fun UserList(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel()
) {
    val users by viewModel.users.observeAsState(emptyList())
    LazyColumn {
        items(users, key = { user -> user.id},) { user ->
            Text(user.username, modifier = Modifier.padding(8.dp))
        }
    }
}