package dev.cc231054.dwitter_ccl3.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowButton(
    modifier: Modifier = Modifier,
    onFollowClick: () -> Unit,
    isAlreadyFollowed: Boolean?
) {
    Button(
        modifier = modifier,
        onClick = { onFollowClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isAlreadyFollowed == false) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                contentDescription = "Follow User",
            )
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                contentDescription = "Unfollow User",
            )
        }
    }
}