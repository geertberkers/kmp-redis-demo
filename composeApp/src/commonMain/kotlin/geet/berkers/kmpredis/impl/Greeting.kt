package geet.berkers.kmpredis.impl

import geet.berkers.kmpredis.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}