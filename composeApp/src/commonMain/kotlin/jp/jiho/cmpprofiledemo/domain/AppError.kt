package jp.jiho.cmpprofiledemo.domain

enum class ErrorKind {
    BadRequest,
    Unauthorized,
    NotFound,
    ServerError,
    Unknown,
}

sealed class AppError(message: String) : Exception(message) {
    data class Http(val statusCode: Int, val kind: ErrorKind, val detail: String) : AppError(detail)
    data object ClientOffline : AppError("オフラインです")
    data class Unknown(val detail: String) : AppError(detail)
}
