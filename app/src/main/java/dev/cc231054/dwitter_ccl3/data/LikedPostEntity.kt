package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable


@Serializable
data class LikedPostEntity (
    val userId: String,
    val postId: Int
)