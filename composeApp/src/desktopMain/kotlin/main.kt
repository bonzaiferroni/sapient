import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import sapient.app.App
import sapient.app.OldApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sapient",
    ) {
        App()
    }
}