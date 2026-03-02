package jp.jiho.cmpprofiledemo.presentation.editprofile

import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.model.Profile

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val notificationEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val savedEvent: Boolean = false,
    val saveError: AppError? = null,
    val fieldErrors: EditProfileFieldErrors = EditProfileFieldErrors.default,
) {
    fun toProfile() = Profile(
        name = name,
        email = email,
        bio = bio,
        profileImageUrl = profileImageUrl,
        notificationEnabled = notificationEnabled,
    )
}
