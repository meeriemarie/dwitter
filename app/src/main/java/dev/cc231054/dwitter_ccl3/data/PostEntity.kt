package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PostEntity(
    val id: Int? = null,
    @Serializable(with = UUIDSerializer::class)
    val userid: UUID,
    val created_at: String? = null,
    val post: String,
    val image: String?,
    val likes: Int? = 0,
)