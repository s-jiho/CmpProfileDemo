package jp.jiho.cmpprofiledemo.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState
        .onStart { loadProfile() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            ProfileUiState(),
        )

    private fun dispatch(event: ProfileEvent) {
        _uiState.update { ProfileStateReducer.reduce(it, event) }
    }

    private suspend fun loadProfile() {
        dispatch(ProfileEvent.LoadingStarted)
        repository.getProfile()
            .onSuccess { dispatch(ProfileEvent.ProfileLoaded(it)) }
            .onFailure {
                val appError = it as? AppError ?: AppError.Unknown(it.message ?: "Unknown error")
                dispatch(ProfileEvent.LoadFailed(appError))
            }
    }
}
