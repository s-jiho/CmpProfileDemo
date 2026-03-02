package jp.jiho.cmpprofiledemo.presentation.editprofile

import cmpprofiledemo.composeapp.generated.resources.Res
import cmpprofiledemo.composeapp.generated.resources.validation_bio_too_long
import cmpprofiledemo.composeapp.generated.resources.validation_email_invalid_format
import cmpprofiledemo.composeapp.generated.resources.validation_email_required
import cmpprofiledemo.composeapp.generated.resources.validation_image_url_invalid
import cmpprofiledemo.composeapp.generated.resources.validation_name_required
import jp.jiho.cmpprofiledemo.presentation.validation.ValidationErrorMessage

enum class EditProfileValidationError : ValidationErrorMessage {
    NameRequired {
        override val errorMessage = Res.string.validation_name_required
    },
    EmailRequired {
        override val errorMessage = Res.string.validation_email_required
    },
    InvalidEmailFormat {
        override val errorMessage = Res.string.validation_email_invalid_format
    },
    BioTooLong {
        override val errorMessage = Res.string.validation_bio_too_long
    },
    InvalidHttpsUrl {
        override val errorMessage = Res.string.validation_image_url_invalid
    },
}
