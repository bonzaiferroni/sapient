package sapient.app.ui.screens

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import sapient.app.io.QuestDao
import sapient.app.ui.core.UiModel
import sapient.app.ui.core.UiState
import sapient.model.quest.Quest

class QuestProfileModel(
    private val id: Int?,
    private val questDao: QuestDao
) : UiModel<QuestProfileState>(QuestProfileState()) {
    init {
        id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val quest = questDao.get(it)
                val parent = quest?.parentId?.let { parentId ->
                    questDao.get(parentId)
                }
                val siblings = parent?.let { questDao.getChildren(it.id) } ?: emptyList()
                sv = sv.copy(quest = quest, parent = parent, siblings = siblings)
            }
        }
        getChildren()
    }

    private fun getChildren() {
        viewModelScope.launch(Dispatchers.IO) {
            val children = if (id == null) {
                questDao.getAvailable()
            } else {
                questDao.getChildren(id)
            }
            sv = sv.copy(children = children)
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
            sv = sv.copy(newTarget = null)
            getChildren()
        }
    }

    fun startChildTarget() {
        sv = sv.copy(newTarget = "")
    }

    fun onToggleComplete(isComplete: Boolean) {
        // update local values
        val value = if (isComplete) System.currentTimeMillis() else null
        sv = sv.copy(
            siblings = sv.siblings.map {
                if (it.id == sv.quest?.id) {
                    it.copy(completedAt = value)
                } else {
                    it
                }
            },
            quest = sv.quest?.copy(completedAt = value)
        )

        // update remote values
        viewModelScope.launch(Dispatchers.IO) {
            val quest = sv.quest ?: return@launch
            questDao.update(quest)
        }
    }
}

data class QuestProfileState(
    val quest: Quest? = null,
    val parent: Quest? = null,
    val siblings: List<Quest> = emptyList(),
    val children: List<Quest> = emptyList(),
    val newTarget: String? = null,
) : UiState
