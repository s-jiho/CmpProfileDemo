package jp.jiho.cmpprofiledemo.presentation.editprofile

import jp.jiho.cmpprofiledemo.domain.AppError

sealed class EditProfileEvent {
    data class NameChanged(val name: String) : EditProfileEvent()
    data class EmailChanged(val email: String) : EditProfileEvent()
    data class BioChanged(val bio: String) : EditProfileEvent()
    data class ImageUrlChanged(val url: String) : EditProfileEvent()
    data class NotificationToggled(val enabled: Boolean) : EditProfileEvent()
    data object SaveErrorDismissed : EditProfileEvent()
    data class ValidationFailed(val errors: EditProfileFieldErrors) : EditProfileEvent()
    data object SavingStarted : EditProfileEvent()
    data object SaveSucceeded : EditProfileEvent()
    data class SaveFailed(val error: AppError) : EditProfileEvent()
}
