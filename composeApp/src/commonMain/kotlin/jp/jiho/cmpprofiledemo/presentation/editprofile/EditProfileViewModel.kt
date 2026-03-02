package jp.jiho.cmpprofiledemo.presentation.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.konform.validation.Invalid
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val validation = EditProfileValidator.validation

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState
        .onStart { loadProfile() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            EditProfileUiState(),
        )

    private fun updateUiState(event: EditProfileEvent) {
        _uiState.update { EditProfileStateReducer.reduce(it, event) }
    }

    private suspend fun loadProfile() {
        repository.getProfile().onSuccess { profile ->
            _uiState.update {
                it.copy(
                    name = profile.name,
                    email = profile.email,
                    bio = profile.bio,
                    profileImageUrl = profile.profileImageUrl,
                    notificationEnabled = profile.notificationEnabled,
                )
            }
        }
    }

    fun onNameChange(name: String) {
        updateUiState(event = EditProfileEvent.NameChanged(name = name))
    }

    fun onEmailChange(email: String) {
        updateUiState(event = EditProfileEvent.EmailChanged(email = email))
    }

    fun onBioChange(bio: String) {
        updateUiState(event = EditProfileEvent.BioChanged(bio = bio))
    }

    fun onImageUrlChange(url: String) {
        updateUiState(event = EditProfileEvent.ImageUrlChanged(url = url))
    }

    fun onNotificationToggle(enabled: Boolean) {
        updateUiState(event = EditProfileEvent.NotificationToggled(enabled = enabled))
    }

    fun onSaveErrorDismissed() {
        updateUiState(event = EditProfileEvent.SaveErrorDismissed)
    }

    fun onSave() {
        val result = validation(_uiState.value)
        if (result is Invalid) {
            val errors = EditProfileValidator.toFieldErrors(result = result)
            updateUiState(event = EditProfileEvent.ValidationFailed(errors = errors))
            return
        }

        viewModelScope.launch {
            updateUiState(event = EditProfileEvent.SavingStarted)

            val profile = _uiState.value.toProfile()
            repository.updateProfile(profile = profile)
                .onSuccess {
                    updateUiState(event = EditProfileEvent.SaveSucceeded)
                }
                .onFailure { error ->
                    val appError = error as? AppError ?: AppError.Unknown(error.message ?: "Unknown error")
                    updateUiState(
                        event = EditProfileEvent.SaveFailed(error = appError)
                    )
                }
        }
    }
}
