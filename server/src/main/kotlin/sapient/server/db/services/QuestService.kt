package sapient.server.db.services

import org.h2.expression.Expression
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Rank
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.wrapAsExpression
import sapient.model.quest.Quest

object Quests : IntIdTable() {
    val mission = reference("mission_id", MissionTable).nullable()
    val parent = reference("parent_id", Quests).nullable()
    val require = reference("require_id", Quests).nullable()
    val target = text("target")
    val notes = text("notes").nullable()
    val completedAt = long("completed_at").nullable()
    val duration = integer("duration").nullable()
    val progress = float("progress").check { it greaterEq 0.0f and (it lessEq 1.0f) }
}

class QuestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : EntityClass<Int, QuestEntity>(Quests)

    var mission by MissionEntity optionalReferencedOn Quests.mission
    var parent by QuestEntity optionalReferencedOn Quests.parent
    var require by QuestEntity optionalReferencedOn Quests.require
    var target by Quests.target
    var notes by Quests.notes
    var completedAt by Quests.completedAt
    var duration by Quests.duration
    // var progress by Quests.progress
}

class QuestService : DataService<Quest, QuestEntity>("quest", QuestEntity) {
    override suspend fun createEntity(data: Quest): (QuestEntity.() -> Unit) {
        val mission = data.missionId?.let { MissionEntity.findById(it) }
        val parent = data.parentId?.let { QuestEntity.findById(it) }
        val require = data.requireId?.let { QuestEntity.findById(it) }
        return {
            this.mission = mission
            this.parent = parent
            this.require = require
            target = data.target
            notes = data.notes
            completedAt = data.completedAt
            duration = data.duration
            // progress = data.progress
        }
    }

    override suspend fun updateEntity(data: Quest): ((QuestEntity) -> Unit) {
        val mission = data.missionId?.let { MissionEntity.findById(it) }
        val parent = data.parentId?.let { QuestEntity.findById(it) }
        val require = data.requireId?.let { QuestEntity.findById(it) }
        return {
            it.mission = mission
            it.parent = parent
            it.require = require
            it.target = data.target
            it.notes = data.notes
            it.completedAt = data.completedAt
            it.duration = data.duration
            // it.progress = data.progress
        }
    }

    override fun QuestEntity.toData() = Quest(
        id = id.value,
        missionId = mission?.id?.value,
        parentId = parent?.id?.value,
        requireId = require?.id?.value,
        target = target,
        notes = notes,
        completedAt = completedAt,
        duration = duration,
        progress = if (QuestEntity.find { Quests.parent eq id }.count() == 0L) {
            0f
        } else {
            QuestEntity.find { Quests.parent eq id and Quests.completedAt.isNotNull()}.count() /
                    QuestEntity.find { Quests.parent eq id }.count().toFloat()
        },
        // childProgress = QuestEntity.find { Quests.parent eq id }.sumOf { it.progress.toDouble() }.toFloat()
        // QuestEntity.find { Quests.parent eq id }.count() / QuestEntity.find { Quests.parent eq id }.count().toFloat(),
    )

    suspend fun getRoots() = dbQuery {
        QuestEntity.find { Quests.parent.isNull() }.map { it.toData() }
    }

    suspend fun getChildren(parentId: Int) = dbQuery {
        QuestEntity.find { Quests.parent eq parentId }.map { it.toData() }
    }

    suspend fun getAvailable() = dbQuery {
        // find all quests that are incomplete and do not have children that are incomplete
        val incompleteChildren = QuestEntity.find {
            Quests.completedAt.isNull() and Quests.parent.isNotNull()
        }.map { it.parent!!.id }
        QuestEntity.find {
            (Quests.completedAt.isNull()) and (Quests.id notInList incompleteChildren)
        }.map { it.toData() }
    }
}