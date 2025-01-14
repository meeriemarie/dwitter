package dev.cc231054.dwitter_ccl3.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.db.UserEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val supabaseUrl = "https://wysgyswdoefgyxubgcdl.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind5c2d5c3dkb2VmZ3l4dWJnY2RsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY3NTg2NDgsImV4cCI6MjA1MjMzNDY0OH0.jyL9vZ-G_3bw8-W0A_7RpaCedztZKyN6nEW1qcCNgR0"

    private val supabase: SupabaseClient by lazy {
        createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Postgrest)
        }
    }

    private val _users = mutableStateOf<List<UserEntity>>(emptyList())
    val users: List<UserEntity> get() = _users.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    fun fetchUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = supabase.from("users").select().decodeList<UserEntity>()
                _users.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}