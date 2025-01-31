package dev.cc231054.dwitter_ccl3.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.ui.components.PostCard
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun SearchScreen(
    currentUserId: UUID,
    onNavigate: (Int?) -> Unit,
    users: List<UserEntity>,
    posts: List<PostEntity>,
    deletePost: (postId: Int) -> Unit,
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {

    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TextField(
            singleLine = true,
            value = searchText,
            onValueChange = { viewModel.onSearchTextChange(it) },
            placeholder = { Text("Search for posts or usernames") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            trailingIcon = {
                IconButton(onClick = { viewModel.onToggleSearch() }) {
                    Icon(
                        contentDescription = if (isSearching) "Clear Search" else "Search",
                        imageVector = if (isSearching) Icons.Default.Close else Icons.Default.Search
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.onToggleSearch()
            })
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            MainSearch(
                currentUserId = currentUserId,
                onNavigate = { onNavigate(it) },
                users = users,
                deletePost = { deletePost(it) },
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun MainSearch(
    currentUserId: UUID,
    users: List<UserEntity>,
    viewModel: UserViewModel,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
) {
    val isSearching by viewModel.isSearching.collectAsState()

    if (isSearching) {
        SearchScreenComponent(
            currentUserId = currentUserId,
            users = users,
            viewModel = viewModel,
            onNavigate = onNavigate,
            deletePost = deletePost,
        )
    }
}


@Composable
fun SearchScreenComponent(
    viewModel: UserViewModel = viewModel(),
    currentUserId: UUID,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var likedPosts by remember { mutableStateOf<List<Int>?>(null) }
    var followedUsers by remember { mutableStateOf<List<UUID>?>(null) }
    LaunchedEffect(currentUserId) {
        coroutineScope.launch {
            likedPosts = viewModel.getLikedPosts(currentUserId)
            followedUsers = viewModel.getFollowedUsers(currentUserId)
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(searchResults, key = { post -> post.id ?: 0 }) { post ->
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
                    isLiked = likedPosts?.contains(post.id) ?: false,
                    onFollowClick = {
                        if (followedUsers?.contains(user.id) == true) {
                            viewModel.unfollowUser(user.id, currentUserId)
                            followedUsers = followedUsers?.filter { it != user.id }
                        } else {
                            viewModel.followUser(user.id, currentUserId)
                            followedUsers = followedUsers?.plus(user.id)
                        }
                    },
                    isFollowed = followedUsers?.contains(user.id) ?: false,
                    likedCount = post.likes
                )
            }
        }

    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel: UserViewModel) {
    TopAppBar(
        title = { Text("Posts") },
        actions = {
            IconButton(onClick = { viewModel.onToggleSearch() }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}

 */