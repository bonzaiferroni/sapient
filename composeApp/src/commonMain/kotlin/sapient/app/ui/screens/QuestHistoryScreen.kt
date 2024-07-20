package sapient.app.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import sapient.app.ui.AppScaffold
import streetlight.app.chopui.ChopScaffold

@Composable
fun QuestHistoryScreen(
    navigator: Navigator?,
) {
    val model = koinViewModel<QuestHistoryModel>()
    val state by model.state

    AppScaffold(
        title = "Quest History",
        navigator = navigator,
    ) {
        LazyColumn {
            items(state.quests) { quest ->
                Text(quest.target)
            }
        }
    }
}