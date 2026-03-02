package jp.jiho.cmpprofiledemo.data.network

import java.net.ConnectException
import java.net.UnknownHostException

actual fun Exception.isOfflineException(): Boolean =
    this is UnknownHostException || this is ConnectException
