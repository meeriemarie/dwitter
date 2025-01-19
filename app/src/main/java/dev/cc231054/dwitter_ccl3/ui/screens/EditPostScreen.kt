package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.ui.BackButton

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentUserId: String,
    upsertPost: (PostEntity) -> Unit
) {
    var postText by remember { mutableStateOf("") }

    val post = PostEntity(
        id = null,
        userid = currentUserId,
        created_at = null,
        post = postText,
        image = null,
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = postText,
            onValueChange = {
                postText = it
            },
            label = { Text("Edit Post") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                upsertPost(post)
            },
        ) {
            Text("Save")
        }
    }
}