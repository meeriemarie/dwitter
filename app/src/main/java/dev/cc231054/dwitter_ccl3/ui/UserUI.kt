package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.cc231054.dwitter_ccl3.db.UserEntity
import io.github.jan.supabase.postgrest.from


@Composable
fun UserList(modifier: Modifier = Modifier) {
    var users by remember { mutableStateOf<List<UserEntity>>(listOf()) }
    LaunchedEffect(Unit) {
        val fetchedUsers = supabase.from("users")
            .select()
            .decodeList<UserEntity>()
        users = fetchedUsers
        }
    LazyColumn {
        items(users, key = { user -> user.id},) { user ->
            Text(user.username, modifier = Modifier.padding(8.dp))
        }
    }
}