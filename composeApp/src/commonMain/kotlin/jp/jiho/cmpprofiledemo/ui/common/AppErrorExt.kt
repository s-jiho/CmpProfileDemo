package jp.jiho.cmpprofiledemo.ui.common

import cmpprofiledemo.composeapp.generated.resources.Res
import cmpprofiledemo.composeapp.generated.resources.error_http_bad_request
import cmpprofiledemo.composeapp.generated.resources.error_http_not_found
import cmpprofiledemo.composeapp.generated.resources.error_http_server_error
import cmpprofiledemo.composeapp.generated.resources.error_http_unauthorized
import cmpprofiledemo.composeapp.generated.resources.error_http_unknown
import cmpprofiledemo.composeapp.generated.resources.error_offline
import cmpprofiledemo.composeapp.generated.resources.error_unknown
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.ErrorKind
import org.jetbrains.compose.resources.StringResource

fun AppError.toUserMessage(): StringResource = when (this) {
    is AppError.ClientOffline -> Res.string.error_offline
    is AppError.Http -> when (kind) {
        ErrorKind.BadRequest -> Res.string.error_http_bad_request
        ErrorKind.Unauthorized -> Res.string.error_http_unauthorized
        ErrorKind.NotFound -> Res.string.error_http_not_found
        ErrorKind.ServerError -> Res.string.error_http_server_error
        ErrorKind.Unknown -> Res.string.error_http_unknown
    }

    is AppError.Unknown -> Res.string.error_unknown
}
