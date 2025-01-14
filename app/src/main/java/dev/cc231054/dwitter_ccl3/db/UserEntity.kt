package dev.cc231054.dwitter_ccl3.db

data class UserEntity (
    val id: Int,
    val name: String,
    val username: String,
    val password: String,
    val email: String
)