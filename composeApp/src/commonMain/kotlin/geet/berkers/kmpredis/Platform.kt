package geet.berkers.kmpredis

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform