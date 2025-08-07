package geet.berkers.kmpredis

import geet.berkers.kmpredis.interfaces.Platform
import geet.berkers.kmpredis.interfaces.RedisClientInterface

expect fun getPlatform(): Platform

expect fun loadRedisUri() : String?

expect fun loadRedisUri(context: Any): String?

expect val client : RedisClientInterface