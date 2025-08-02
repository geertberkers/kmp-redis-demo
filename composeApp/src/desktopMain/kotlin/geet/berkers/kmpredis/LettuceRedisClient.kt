package geet.berkers.kmpredis

import geet.berkers.kmpredis.redis.LettuceRedisClient

actual val client: LettuceRedisClient
    get() = LettuceRedisClient()