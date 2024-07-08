import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import sapient.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sapient",
    ) {
        App()
    }
}