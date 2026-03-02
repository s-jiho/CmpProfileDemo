package jp.jiho.cmpprofiledemo.presentation.profile

object ProfileStateReducer {
    fun reduce(state: ProfileUiState, event: ProfileEvent): ProfileUiState = when (event) {
        is ProfileEvent.LoadingStarted -> state.copy(
            isLoading = true,
            profile = null,
            error = null,
        )
        is ProfileEvent.ProfileLoaded -> state.copy(
            isLoading = false,
            profile = event.profile,
            error = null,
        )
        is ProfileEvent.LoadFailed -> state.copy(
            isLoading = false,
            error = event.error,
        )
    }
}
