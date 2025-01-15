package dev.cc231054.dwitter_ccl3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dev.cc231054.dwitter_ccl3.ui.UserList
import dev.cc231054.dwitter_ccl3.ui.theme.Dwitter_CCL3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dwitter_CCL3Theme {
                MainScreen()
            }
        }
    }
}
