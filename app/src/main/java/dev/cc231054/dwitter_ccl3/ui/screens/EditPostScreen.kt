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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cc231054.dwitter_ccl3.ui.BackButton

@Composable
fun EditPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = "",
            onValueChange = {},
            label = { Text("Edit Post") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                /*TODO*/
                Log.d("EditPostScreen", "Save button clicked")
            },
        ) {
            Text("Save")
        }
    }
}