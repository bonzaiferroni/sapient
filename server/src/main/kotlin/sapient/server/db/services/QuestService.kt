package sapient.server.db.services

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import sapient.model.quest.Quest
import sapient.server.db.DataService

object QuestTable : IntIdTable() {
    val mission = reference("mission_id", MissionTable)
    val parent = reference("parent_id", QuestTable).nullable()
    val target = text("target")
    val notes = text("notes")
    val completedAt = long("completed_at").nullable()
    val duration = integer("duration")
}

class QuestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, QuestEntity>(QuestTable)

    var mission by MissionEntity referencedOn QuestTable.mission
    var parent by QuestEntity optionalReferencedOn QuestTable.parent
    var target by QuestTable.target
    var notes by QuestTable.notes
    var completedAt by QuestTable.completedAt
    var duration by QuestTable.duration
}

class QuestService : DataService<Quest, QuestEntity>("quest", QuestEntity) {
    override suspend fun createEntity(data: Quest): (QuestEntity.() -> Unit)? {
        val mission = MissionEntity.findById(data.missionId) ?: return null
        val parent = data.parentId?.let { QuestEntity.findById(it) }
        return {
            this.mission = mission
            this.parent = parent
            target = data.target
            notes = data.notes
            completedAt = data.completedAt
            duration = data.duration
        }
    }

    override suspend fun updateEntity(data: Quest): ((QuestEntity) -> Unit)? {
        val mission = MissionEntity.findById(data.missionId) ?: return null
        val parent = data.parentId?.let { QuestEntity.findById(it) }
        return {
            it.mission = mission
            it.parent = parent
            it.target = data.target
            it.notes = data.notes
            it.completedAt = data.completedAt
            it.duration = data.duration
        }
    }

    override fun QuestEntity.toData() = Quest(
        id = id.value,
        missionId = mission.id.value,
        parentId = parent?.id?.value,
        target = target,
        notes = notes,
        completedAt = completedAt,
        duration = duration
    )

}