package sapient.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import sapient.app.core.UiModel
import sapient.app.core.UiState
import sapient.app.io.QuestDao
import sapient.model.quest.Quest
import streetlight.app.chopui.Scaffold

@Composable
fun QuestListScreen(navigator: Navigator?) {
    Scaffold("quests", navigator) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World!")
        }
    }
}

class QuestListModel(
    private val questDao: QuestDao
) : UiModel<QuestListState>(QuestListState()) {

}

data class QuestListState(
    val quests: List<Quest> = emptyList()
) : UiState

