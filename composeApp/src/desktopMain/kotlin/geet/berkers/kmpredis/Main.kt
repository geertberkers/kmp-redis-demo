package geet.berkers.kmpredis

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import geet.berkers.kmpredis.screens.App


fun main() = application {
    val uri = loadRedisUri(this) ?: ""

    Window(onCloseRequest = ::exitApplication, title = "Redis Client") {
        App(redisConnectionUri = uri) // This should be your main @Composable function
    }
}