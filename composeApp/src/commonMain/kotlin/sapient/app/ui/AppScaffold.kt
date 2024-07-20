package sapient.app.ui

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.Navigator
import sapient.app.Scenes
import streetlight.app.chopui.ChopScaffold
import streetlight.app.chopui.FabConfig

@Composable
fun AppScaffold(
    title: String,
    navigator: Navigator?,
    fabConfig: FabConfig? = null,
    content: @Composable () -> Unit,
) {
    ChopScaffold(
        title = title,
        navigator = navigator,
        routes = listOf(Scenes.questProfile.route, Scenes.questHistory.route),
        fabConfig = fabConfig,
    ) {
        content()
    }
}