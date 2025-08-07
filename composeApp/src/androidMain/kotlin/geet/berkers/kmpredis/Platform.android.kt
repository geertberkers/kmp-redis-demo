package geet.berkers.kmpredis

import android.os.Build
import geet.berkers.kmpredis.interfaces.Platform

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()