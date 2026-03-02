package jp.jiho.cmpprofiledemo.presentation.profile

import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.model.Profile

sealed class ProfileEvent {
    data object LoadingStarted : ProfileEvent()
    data class ProfileLoaded(val profile: Profile) : ProfileEvent()
    data class LoadFailed(val error: AppError) : ProfileEvent()
}
