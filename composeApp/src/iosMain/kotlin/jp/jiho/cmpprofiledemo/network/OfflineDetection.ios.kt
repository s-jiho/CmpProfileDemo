package jp.jiho.cmpprofiledemo.data.network

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet

actual fun Exception.isOfflineException(): Boolean {
    if (this is DarwinHttpRequestException) {
        val code = origin.code
        return code == NSURLErrorNotConnectedToInternet
                || code == NSURLErrorCannotFindHost
                || code == NSURLErrorNetworkConnectionLost
    }
    return false
}
