package dev.cc231054.dwitter_ccl3.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.ui.BackButton
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import java.util.UUID

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    currentUserId: UUID,
    postId: Int?,
    upsertPost: (PostEntity) -> Unit,
    onBackButton: () -> Unit,
) {

    var post by remember { mutableStateOf<PostEntity?>(null) }
    var postText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        post = userViewModel.tryFetchPostById(postId)
        postText = post?.post ?: ""
    }

    val toUpsertPost = PostEntity(
        id = post?.id,
        userid = currentUserId,
        created_at = post?.created_at,
        post = postText,
        image = post?.image,
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBackButton = onBackButton)

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
                upsertPost(toUpsertPost)
                Log.d("EditPostScreen", "Post: $post")
                onBackButton()
            },
        ) {
            Text("Save")
        }
    }
}