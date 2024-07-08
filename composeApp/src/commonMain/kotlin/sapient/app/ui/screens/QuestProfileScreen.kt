package sapient.app.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import sapient.app.io.QuestDao
import sapient.app.ui.core.UiModel
import sapient.app.ui.core.UiState
import sapient.model.quest.Quest
import streetlight.app.chopui.BoxScaffold

@Composable
fun QuestProfileScreen(
    id: Int?,
    navigator: Navigator?
) {
    val model = koinViewModel(QuestProfileModel::class) { parametersOf(id)}
    val state by model.state
    BoxScaffold(
        title = "quest",
        navigator = navigator,
        floatingAction = model::startChildTarget
    ) {
        state.parent?.let {
            Button(onClick = { navigator?.navigate("quest/${it.id}") }) {
                Text(it.target)
            }
        }
        state.quest?.let {
            Card {
                Text(it.target)
            }
        }
        LazyColumn {
            items(state.children) { quest ->
                Button(onClick = { navigator?.navigate("quest/${quest.id}") }) {
                    Text(quest.target)
                }
            }
        }
        state.newTarget?.let {
            Row {
                TextField(it, onValueChange = model::childTargetUpdate )
                Button(onClick = model::addChildTarget) {
                    Text("Add")
                }
            }
        }
    }
}

class QuestProfileModel(
    private val id: Int?,
    private val questDao: QuestDao
) : UiModel<QuestProfileState>(QuestProfileState()) {
    init {
        id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val quest = questDao.get(it)
                quest?.parentId?.let { parentId ->
                    val parent = questDao.get(parentId)
                    sv = sv.copy(parent = parent)
                }
                sv = sv.copy(quest = quest)
            }
        }
        getChildren()
    }

    private fun getChildren() {
        viewModelScope.launch(Dispatchers.IO) {
            val roots = questDao.getRoots()
            sv = sv.copy(children = roots)
        }
    }

    fun childTargetUpdate(value: String) {
        sv = sv.copy(newTarget = value)
    }

    fun addChildTarget() {
        viewModelScope.launch(Dispatchers.IO) {
            val newTarget = sv.newTarget ?: return@launch
            val quest = Quest(target = newTarget, parentId = sv.quest?.id)
            questDao.create(quest)
            getChildren()
        }
    }

    fun startChildTarget() {
        println("ey")
        sv = sv.copy(newTarget = "")
    }
}

data class QuestProfileState(
    val quest: Quest? = null,
    val parent: Quest? = null,
    val children: List<Quest> = emptyList(),
    val newTarget: String? = null,
) : UiState

