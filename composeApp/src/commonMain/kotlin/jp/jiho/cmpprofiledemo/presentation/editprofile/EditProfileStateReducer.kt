package jp.jiho.cmpprofiledemo.presentation.editprofile

object EditProfileStateReducer {
    fun reduce(state: EditProfileUiState, event: EditProfileEvent): EditProfileUiState = when (event) {
        is EditProfileEvent.NameChanged -> state.copy(
            name = event.name,
            fieldErrors = state.fieldErrors.copy(nameError = null)
        )

        is EditProfileEvent.EmailChanged -> state.copy(
            email = event.email,
            fieldErrors = state.fieldErrors.copy(emailError = null)
        )

        is EditProfileEvent.BioChanged -> state.copy(
            bio = event.bio,
            fieldErrors = state.fieldErrors.copy(bioError = null)
        )

        is EditProfileEvent.ImageUrlChanged -> state.copy(
            profileImageUrl = event.url,
            fieldErrors = state.fieldErrors.copy(profileImageUrlError = null)
        )

        is EditProfileEvent.NotificationToggled -> state.copy(
            notificationEnabled = event.enabled
        )

        is EditProfileEvent.SaveErrorDismissed -> state.copy(
            saveError = null
        )

        is EditProfileEvent.ValidationFailed -> state.copy(
            fieldErrors = event.errors
        )

        is EditProfileEvent.SavingStarted -> state.copy(
            isSaving = true,
            saveError = null,
            fieldErrors = EditProfileFieldErrors.default,
        )

        is EditProfileEvent.SaveSucceeded -> state.copy(
            isSaving = false,
            savedEvent = true
        )

        is EditProfileEvent.SaveFailed -> state.copy(
            isSaving = false,
            saveError = event.error
        )
    }
}
