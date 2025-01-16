package dev.cc231054.dwitter_ccl3.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cc231054.dwitter_ccl3.db.PostEntity
import dev.cc231054.dwitter_ccl3.db.UserEntity
import dev.cc231054.dwitter_ccl3.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch


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


    private val _posts = MutableLiveData<List<PostEntity>>()
    val posts: LiveData<List<PostEntity>> get() = _posts

    init {
        viewModelScope.launch {
            try {
                val fetchedPosts = supabase.from("posts")
                    .select()
                    .decodeList<PostEntity>()
                _posts.value = fetchedPosts
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error: ${e.message}")
            }
        }
    }
}