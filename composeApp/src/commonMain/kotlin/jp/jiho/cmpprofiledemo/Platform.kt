package jp.jiho.cmpprofiledemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform