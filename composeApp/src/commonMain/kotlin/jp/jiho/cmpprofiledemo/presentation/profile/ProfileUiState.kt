package jp.jiho.cmpprofiledemo.presentation.profile

import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.model.Profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val error: AppError? = null,
)
