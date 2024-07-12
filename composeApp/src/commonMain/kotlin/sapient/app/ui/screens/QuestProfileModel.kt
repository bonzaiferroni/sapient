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
        getQuests()
    }

    private fun getQuests() {
        viewModelScope.launch(Dispatchers.IO) {
            val available = questDao.getAvailable()
            val quest = id?.let { questDao.get(it) }
            val children = id?.let { questDao.getChildren(it) } ?: emptyList()
            val parent = quest?.parentId?.let { parentId ->
                questDao.get(parentId)
            }
            val siblings = parent?.let { questDao.getChildren(it.id) } ?: emptyList()
            sv = sv.copy(
                available = available,
                quest = quest,
                children = children,
                parent = parent,
                siblings = siblings,
                stepProgress = children.count { it.isCompleted }.toFloat() / children.size
            )
        }
    }

    fun updateNewQuest(value: String) {
        sv = sv.copy(newQuest = value)
    }

    fun addNewQuest() {
        viewModelScope.launch(Dispatchers.IO) {
            val newTarget = sv.newQuest ?: return@launch
            val quest = Quest(target = newTarget, parentId = if (sv.newIsChild) id else null)
            questDao.create(quest)
            cancelNewQuest()
            getQuests()
        }
    }

    fun startAddChild() {
        sv = sv.copy(newQuest = "", newIsChild = true)
    }

    fun startAddParentless() {
        sv = sv.copy(newQuest = "", newIsChild = false)
    }

    fun cancelNewQuest() {
        sv = sv.copy(newQuest = null)
    }

    fun onToggleComplete(quest: Quest, isComplete: Boolean) {
        // update local values
        val value = if (isComplete) System.currentTimeMillis() else null
        val updatedQuest = quest.copy(completedAt = value)
        sv = sv.copy(
            siblings = if (sv.siblings.any{ it.id == quest.id }) {
                sv.siblings.map { if (it.id == quest.id) updatedQuest else it }
            } else sv.siblings,
            quest = if (sv.quest?.id == quest.id) updatedQuest else sv.quest,
            children = if (sv.children.any{ it.id == quest.id }) {
                sv.children.map { if (it.id == quest.id) updatedQuest else it }
            } else sv.children
        )

        // update remote values
        viewModelScope.launch(Dispatchers.IO) {
            questDao.update(updatedQuest)
        }
    }
}

data class QuestProfileState(
    val quest: Quest? = null,
    val parent: Quest? = null,
    val siblings: List<Quest> = emptyList(),
    val children: List<Quest> = emptyList(),
    val available: List<Quest> = emptyList(),
    val stepProgress: Float = 0f,
    val newQuest: String? = null,
    val newIsChild: Boolean = false,
) : UiState
