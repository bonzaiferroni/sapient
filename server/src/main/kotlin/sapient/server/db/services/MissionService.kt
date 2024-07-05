package sapient.server.db.services

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import sapient.model.quest.Mission
import sapient.server.db.DataService

object MissionTable : IntIdTable() {
    val target = text("target")
}

class MissionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, MissionEntity>(MissionTable)

    var target by MissionTable.target
}

class MissionService : DataService<Mission, MissionEntity>("mission", MissionEntity) {
    override suspend fun createEntity(data: Mission): MissionEntity.() -> Unit = {
        target = data.target
    }

    override suspend fun updateEntity(data: Mission): (MissionEntity) -> Unit {
        return {
            it.target = data.target
        }
    }

    override fun MissionEntity.toData() = Mission(
        id = id.value,
        target = target
    )
}