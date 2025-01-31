package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.components.PostList
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import java.util.UUID

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    onNavigate: (Int?) -> Unit,
    users: List<UserEntity>,
    posts: List<PostEntity>,
    deletePost: (postId: Int) -> Unit,
    viewModel: UserViewModel,

    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        PostList(
            modifier = modifier,
            currentUserId = currentUserId,
            onNavigate = { onNavigate(it) },
            posts = posts,
            users = users,
            deletePost = { deletePost(it) },
            viewModel = viewModel
        )
    }
}