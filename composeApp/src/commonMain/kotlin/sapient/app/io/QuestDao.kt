package sapient.app.io

import sapient.app.ApiClient
import sapient.model.quest.Quest

class QuestDao(
    private val client: ApiClient,
) {
    suspend fun create(quest: Quest): Int = client.create("/quest", quest)
    suspend fun getAll(): List<Quest> = client.getBody("/quest")
    suspend fun get(id: Int): Quest? = client.getBody("/quest/$id")
    suspend fun update(quest: Quest): Boolean = client.update("/quest", quest.id, quest)
    suspend fun delete(id: Int): Boolean = client.delete("/quest", id)
    suspend fun getRoots(): List<Quest> = client.getBody("/quest/roots")
    suspend fun getChildren(id: Int): List<Quest> = client.getBody("/quest/$id/children")
    suspend fun getAvailable(): List<Quest> = client.getBody("/quest/available")
}