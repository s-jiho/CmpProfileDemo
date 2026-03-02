package jp.jiho.agp9template

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform