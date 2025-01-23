package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import dev.cc231054.dwitter_ccl3.data.PostEntity
import dev.cc231054.dwitter_ccl3.data.UserEntity
import dev.cc231054.dwitter_ccl3.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

//todo: very messy, should separate all (related) composable into separate files
@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit,
    isAlreadyLiked: Boolean?
) {
    IconButton(
        onClick = onLikeClick,
        modifier.padding(6.dp)
    ) {
        Icon(
            imageVector = if (isAlreadyLiked == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            tint = if (isAlreadyLiked == true) Color.Red else Color.White,
            contentDescription = if (isAlreadyLiked == true) "Unlike Post" else "Like Post"
        )
    }
}

@Composable
fun FollowButton(
    modifier: Modifier = Modifier,
    onFollowClick: () -> Unit,
    isAlreadyFollowed: Boolean?
) {
    Button(
        modifier = modifier,
        onClick = {
            onFollowClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAlreadyFollowed == false) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = "Follow User",
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Follow",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = "Unfollow User",
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Followed",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}


@Composable
fun AddPostButton(
    modifier: Modifier = Modifier,
    onNavigate : () -> Unit,
) {
    Button(
        modifier = modifier.size(72.dp),
        onClick = {
            onNavigate()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Add Post",
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBackButton: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = { onBackButton() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = "Cancel", color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (Int) -> Unit,
    viewModel: UserViewModel
) {
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
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts, key = { post -> post.id ?: 0 }) { post ->
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
                    isFollowed = followedUsers?.contains(user.id) ?: false
                )
            }
        }
    }
}

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    currentUserId: UUID,
    post: PostEntity,
    user: UserEntity,
    deletePost: () -> Unit,
    onNavigate: (Int?) -> Unit,
    onLikeClick: () -> Unit,
    onFollowClick: () -> Unit,
    isLiked: Boolean,
    isFollowed: Boolean
) {
    var showFullText by remember {
        mutableStateOf(false)
    }

    var showDeletePostDialog by remember {
        mutableStateOf(false)
    }

    if (showDeletePostDialog) {
        DeletePostDialog(
            onConfirm = {
                deletePost()
                showDeletePostDialog = false
            },
            onDismiss = {
                showDeletePostDialog = false
            }
        )
    }


    Card(
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = if (user.id.toString() == currentUserId.toString()) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }
    ) {
        Column {
            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)
            ) {

                Row {
                    Image(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        painter = rememberAsyncImagePainter(user.avatar_url),
                        contentDescription = "user avatar"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                        ) {
                            append(user.name)
                        }

                        append("\n")

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                        ) {
                            append(user.username)
                        }
                    }

                    Text(text = annotatedString)

                    if (user.id.toString() == currentUserId.toString()) {
                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            Icon(
                                modifier = Modifier.clickable {
                                    showDeletePostDialog = true
                                },
                                imageVector = Icons.Outlined.Delete,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Delete Post",
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Icon(
                                modifier = Modifier.clickable {
                                    onNavigate(post.id)
                                },
                                imageVector = Icons.Default.Edit,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Edit Post",
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))

                        FollowButton(
                            onFollowClick = { onFollowClick() },
                            isAlreadyFollowed = isFollowed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    modifier = Modifier.clickable {
                        showFullText = !showFullText
                    },
                    text = post.post,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (showFullText) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (post.image != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    painter = rememberAsyncImagePainter(post.image),
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop
                )
            }
            if (user.id.toString() != currentUserId.toString()) {
                LikeButton(
                    onLikeClick = { onLikeClick() },
                    isAlreadyLiked = isLiked
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePostDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Confirm Delete",
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Are you sure you want to delete this post?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onConfirm() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}