package dev.cc231054.dwitter_ccl3.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.util.UUID

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
    isFollowed: Boolean,
    likedCount: Int?
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
        modifier = Modifier
            .animateContentSize()
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            ),
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
                        painter = if (user.avatar_url != "") {
                            rememberAsyncImagePainter(user.avatar_url)
                        } else {
                            rememberAsyncImagePainter("https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg")
                        },
                        contentDescription = "user avatar",
                        contentScale = ContentScale.Crop
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
                            append("@${user.username}")
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
            if (post.userid.toString() != currentUserId.toString()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LikeButton(
                        onLikeClick = { onLikeClick() },
                        isAlreadyLiked = isLiked
                    )
                    Text(text = "$likedCount")
                }
            } else {
                Text(
                    text = "$likedCount like(s)",
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }
}