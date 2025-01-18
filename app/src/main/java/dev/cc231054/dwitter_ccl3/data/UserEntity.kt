package dev.cc231054.dwitter_ccl3.data

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class UserEntity (
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val username: String,
    val email: String,
    val avatar_url: String
)