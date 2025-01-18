package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable


@Serializable
data class UserEntity (
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val avatar_url: String,
)