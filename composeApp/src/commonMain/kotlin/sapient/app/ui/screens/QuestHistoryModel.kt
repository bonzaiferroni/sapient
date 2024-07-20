package sapient.app.ui.screens

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import sapient.app.io.QuestDao
import sapient.app.ui.core.UiModel
import sapient.app.ui.core.UiState
import sapient.model.quest.Quest
import sapient.model.quest.QuestDto

class QuestHistoryModel(
    private val questDao: QuestDao
): UiModel<QuestHistoryState>(QuestHistoryState()) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val completed = questDao.getCompleted()
            sv = sv.copy(quests = completed)
        }
    }
}

data class QuestHistoryState(
    val quests: List<Quest> = emptyList()
) : UiState