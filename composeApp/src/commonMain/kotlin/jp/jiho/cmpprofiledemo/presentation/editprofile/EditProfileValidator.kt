package jp.jiho.cmpprofiledemo.presentation.editprofile

import io.konform.validation.Invalid
import io.konform.validation.Validation
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern
import io.konform.validation.filterPath
import kotlin.reflect.KProperty1

data class EditProfileFieldErrors(
    val nameError: EditProfileValidationError? = null,
    val emailError: EditProfileValidationError? = null,
    val bioError: EditProfileValidationError? = null,
    val profileImageUrlError: EditProfileValidationError? = null,
) {
    companion object {
        val default = EditProfileFieldErrors()
    }
}

object EditProfileValidator {
    val validation = Validation {
        EditProfileUiState::name {
            notBlank() userContext EditProfileValidationError.NameRequired
        }
        EditProfileUiState::email {
            notBlank() userContext EditProfileValidationError.EmailRequired
            pattern(
                Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
            ) userContext EditProfileValidationError.InvalidEmailFormat
        }
        EditProfileUiState::bio {
            constrain("bio must be 100 chars or less") { bio ->
                bio.count { !it.isLowSurrogate() } <= 100
            } userContext EditProfileValidationError.BioTooLong
        }
        EditProfileUiState::profileImageUrl {
            constrain("image url must be https") {
                it.isEmpty() || Regex("^https://\\S+$").matches(it)
            } userContext EditProfileValidationError.InvalidHttpsUrl
        }
    }

    fun toFieldErrors(result: Invalid) = EditProfileFieldErrors(
        nameError = result.firstErrorFor(EditProfileUiState::name),
        emailError = result.firstErrorFor(EditProfileUiState::email),
        bioError = result.firstErrorFor(EditProfileUiState::bio),
        profileImageUrlError = result.firstErrorFor(EditProfileUiState::profileImageUrl),
    )

    private fun Invalid.firstErrorFor(prop: KProperty1<EditProfileUiState, *>): EditProfileValidationError? {
        return errors
            .filterPath(prop)
            .firstOrNull()?.userContext as? EditProfileValidationError
    }
}
