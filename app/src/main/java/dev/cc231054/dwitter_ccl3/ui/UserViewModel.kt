package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.db.UserEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

val supabaseUrl = "https://wysgyswdoefgyxubgcdl.supabase.co"
val supabaseKey =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Ind5c2d5c3dkb2VmZ3l4dWJnY2RsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzY3NTg2NDgsImV4cCI6MjA1MjMzNDY0OH0.jyL9vZ-G_3bw8-W0A_7RpaCedztZKyN6nEW1qcCNgR0"

val supabase = createSupabaseClient(
    supabaseUrl, supabaseKey
) {
    install(Postgrest)
    install(Auth)
}

class UserViewModel(): ViewModel() {
    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

    init {
        viewModelScope.launch {
            try {
                val fetchedUsers = supabase.from("users")
                    .select()
                    .decodeList<UserEntity>()
                _users.value = fetchedUsers
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error: ${e.message}")
            }
        }
    }
}