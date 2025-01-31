package dev.cc231054.dwitter_ccl3.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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