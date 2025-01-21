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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

//todo: very messy, should separate all (related) composable into separate files

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
            containerColor = Color(0xFF454E62)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            tint = Color.White,
            contentDescription = "Add Post",
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBackButton : () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = {onBackButton()},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF454E62)
        )
    ) {
        Text(text = "Cancel", color = Color.White)
    }
}

@Composable
fun PostList(
    modifier: Modifier = Modifier,
    currentUserId: String,
    posts: List<PostEntity>,
    users: List<UserEntity>,
    onNavigate: (Int?) -> Unit,
    deletePost: (postId: Int) -> Unit,
) {


    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(posts, key = { post -> post.id ?: 0 }) { post ->
            val user = users.find { it.id == post.userid }
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
                    onNavigate = { onNavigate(it) }
                )
            }
        }
    }
}

@Composable
fun PostCard (
    modifier: Modifier = Modifier,
    currentUserId: String,
    post: PostEntity,
    user: UserEntity,
    deletePost : () -> Unit,
    onNavigate: (Int?) -> Unit
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF544D79)
        )
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
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        ) {
                            append(user.name)
                        }

                        append("\n")

                        withStyle(
                            style = SpanStyle(
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 16.sp
                            )
                        ) {
                            append(user.username)
                        }
                    }

                    Text(text = annotatedString)

                    if (user.id == currentUserId) {
                        Spacer(modifier = Modifier.weight(1f))

                        Column {
                            Icon(
                                modifier = Modifier.clickable {
                                    showDeletePostDialog = true
                                },
                                imageVector = Icons.Outlined.Delete,
                                tint = Color.Red,
                                contentDescription = "Delete Post",
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Icon(
                                modifier = Modifier.clickable {
                                    onNavigate(post.id)
                                },
                                imageVector = Icons.Default.Edit,
                                tint = Color.White,
                                contentDescription = "Edit Post",
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    modifier = Modifier.clickable {
                        showFullText = !showFullText
                    },
                    text = post.post,
                    color = Color.White.copy(alpha = 0.75f),
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
            color = Color(0xFF454E62),
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
                    color = Color.White
                )

                Text(
                    text = "Are you sure you want to delete this post?",
                    color = Color.White.copy(alpha = 0.75f)
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
                            containerColor = Color(0xFF544D79)
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onConfirm() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF544D79)
                        )
                    ) {
                        Text(
                            text = "Delete",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}