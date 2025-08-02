

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.application

import androidx.compose.ui.window.Window
fun main() = application {
    val client = LettuceRedisClient()
    Window(onCloseRequest = ::exitApplication, title = "Redis Client") {
        App(client) // This should be your main @Composable function
    }
}
