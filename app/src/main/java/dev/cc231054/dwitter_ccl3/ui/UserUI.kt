package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.UserEntity


@Composable
fun ContentScreen(modifier: Modifier = Modifier) {
    UserList()
}

@Composable
fun UserList(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.users.collectAsStateWithLifecycle()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
        items(state) {
            user: UserEntity ->
            Spacer(Modifier.padding(8.dp))
            Text(text = user.username)
            Text(text = "@${user.name}")
        }
    }
}