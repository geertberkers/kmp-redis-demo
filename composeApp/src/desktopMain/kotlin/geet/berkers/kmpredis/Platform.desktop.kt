package geet.berkers.kmpredis

class DesktopPlatform: Platform {
    override val name: String = "Desktop Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = DesktopPlatform()