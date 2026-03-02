package jp.jiho.cmpprofiledemo.data.network

import io.ktor.client.statement.HttpResponse
import jp.jiho.cmpprofiledemo.domain.AppError
import jp.jiho.cmpprofiledemo.domain.ErrorKind

fun HttpResponse.toAppError(): AppError {
    val kind = when (status.value) {
        400 -> ErrorKind.BadRequest
        401 -> ErrorKind.Unauthorized
        404 -> ErrorKind.NotFound
        in 500..599 -> ErrorKind.ServerError
        else -> ErrorKind.Unknown
    }
    return AppError.Http(statusCode = status.value, kind = kind, detail = "${status.value} ${status.description}")
}

fun Exception.toAppError(): AppError {
    if (isOfflineException()) return AppError.ClientOffline
    return AppError.Unknown(message ?: "unknown exception: ${this::class.simpleName}")
}
