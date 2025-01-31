package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.components.FollowedPosts
import dev.cc231054.dwitter_ccl3.ui.components.PostList
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import java.util.UUID

enum class HomeTab {
    Discover,
    Followed
}

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
    var activeTab by remember { mutableStateOf(HomeTab.Discover) }

    LaunchedEffect(activeTab) {
        viewModel.getFollowedPosts(currentUserId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = { activeTab = HomeTab.Discover }) {
                Text(
                    "Discover",
                    textDecoration = if (activeTab == HomeTab.Discover) TextDecoration.Underline else TextDecoration.None
                )
            }
            TextButton(onClick = { activeTab = HomeTab.Followed }) {
                Text(
                    "Followed",
                    textDecoration = if (activeTab == HomeTab.Followed) TextDecoration.Underline else TextDecoration.None
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                HomeTab.Discover -> {
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

                HomeTab.Followed -> {
                    FollowedPosts(
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
        }
    }
}