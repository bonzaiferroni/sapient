package sapient.server.utilities

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import sapient.model.quest.Quest
import sapient.server.db.services.QuestService
import java.io.File
import kotlin.reflect.KClass

object DbBackup {

    suspend fun create() {
        val quests = QuestService().readAll()

        val backup = DataBackup(quests)
        val json = Json.encodeToString(backup)
        File("backup.json").writeText(json)
    }

    suspend fun restore() {
        val file = File("backup.json")
        if (!file.exists()) {
            println("backup not found")
            return
        }
        val json = file.readText()
        val backup = Json.decodeFromString<DataBackup>(json)

        backup.quests.forEach {
//            val locationId = IdMap.getNewId(Quest::class, it.parentId)
//                ?: throw IllegalStateException("Location not found")
//            val userId = IdMap.getNewId(User::class, it.userId)
//                ?: throw IllegalStateException("User not found")
            val questId = QuestService().create(it.copy(parentId = null))
            IdMap.setNewId(Quest::class, it.id, questId)
        }
        // assign parent ids
        backup.quests.forEach {
            if (it.parentId != null) {
                val parentId = IdMap.getNewId(Quest::class, it.parentId!!)
                    ?: throw IllegalStateException("Parent not found")
                QuestService().update(it.id, it.copy(parentId = parentId))
            }
        }
    }
}

object IdMap {
    private val map = mutableMapOf<KClass<*>, MutableMap<Int, Int>>()

    fun getNewId(type: KClass<*>, previousId: Int): Int? {
        return map[type]?.get(previousId)
    }

    fun setNewId(type: KClass<*>, previousId: Int, newId: Int) {
        val childMap = map.getOrPut(type) { mutableMapOf() }
        childMap[previousId] = newId
    }
}

@Serializable
data class DataBackup(
    val quests: List<Quest>,
)