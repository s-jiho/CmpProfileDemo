package jp.jiho.cmpprofiledemo.data.dto

import jp.jiho.cmpprofiledemo.domain.model.Profile
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val bio: String,
    val profileImageUrl: String,
    val notificationEnabled: Boolean,
)

fun Profile.toRequest() = UpdateProfileRequest(
    name = name,
    email = email,
    bio = bio,
    profileImageUrl = profileImageUrl,
    notificationEnabled = notificationEnabled,
)
