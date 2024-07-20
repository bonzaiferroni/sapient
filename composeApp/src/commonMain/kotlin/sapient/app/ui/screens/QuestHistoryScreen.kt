package sapient.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import sapient.app.ui.AppScaffold
import streetlight.app.chopui.ChopScaffold
import streetlight.app.chopui.addBasePadding

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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .addBasePadding()
        ) {
            items(state.quests) { quest ->
                Button(
                    onClick = {
                        navigator?.navigate("/quest/${quest.id}")
                    }
                ) {
                    Text(quest.target)
                }
            }
        }
    }
}