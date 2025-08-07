package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.Platform

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()