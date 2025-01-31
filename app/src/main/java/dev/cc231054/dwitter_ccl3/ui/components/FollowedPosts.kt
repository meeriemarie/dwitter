package dev.cc231054.dwitter_ccl3.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun FollowedPosts(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
    viewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var usersFollowedPosts by remember { mutableStateOf<List<PostEntity>>(emptyList()) }
    var likedPosts by remember { mutableStateOf<List<Int>?>(null) }
    var followedUsers by remember { mutableStateOf<List<UUID>?>(null) }
    LaunchedEffect(currentUserId) {
        coroutineScope.launch {
            likedPosts = viewModel.getLikedPosts(currentUserId)
            followedUsers = viewModel.getFollowedUsers(currentUserId)
            usersFollowedPosts = viewModel.getFollowedPosts(currentUserId)
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(usersFollowedPosts, key = { post -> post.id ?: 0 }) { post ->
            val user = users.find { it.id.toString() == post.userid.toString() }
            if (user != null) {
                PostCard(
                    post = post,
                    user = user,
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.8f),
                    deletePost = {
                        deletePost(post.id!!)
                    },
                    onNavigate = { onNavigate(it) },
                    onLikeClick = {
                        if (likedPosts?.contains(post.id) == true) {
                            viewModel.unlikePost(currentUserId, post.id!!)
                            likedPosts = likedPosts?.filter { it != post.id }
                        } else {
                            viewModel.likePost(post.id!!, currentUserId)
                            likedPosts = likedPosts?.plus(post.id)
                        }
                    },
                    onFollowClick = {
                        if (followedUsers?.contains(user.id) == true) {
                            viewModel.unfollowUser(user.id, currentUserId)
                            followedUsers = followedUsers?.filter { it != user.id }
                        } else {
                            viewModel.followUser(user.id, currentUserId)
                            followedUsers = followedUsers?.plus(user.id)
                        }

                    },
                    isLiked = likedPosts?.contains(post.id) ?: false,
                    isFollowed = followedUsers?.contains(user.id) ?: false,
                    likedCount = post.likes
                )
            }
        }
    }
}