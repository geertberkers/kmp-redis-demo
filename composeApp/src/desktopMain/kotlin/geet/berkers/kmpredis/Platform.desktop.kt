package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.Platform

class DesktopPlatform: Platform {
    override val name: String = "Desktop Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = DesktopPlatform()