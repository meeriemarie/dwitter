package dev.cc231054.dwitter_ccl3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cc231054.dwitter_ccl3.ui.theme.Dwitter_CCL3Theme

class MainNavigation : ComponentActivity() {
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
