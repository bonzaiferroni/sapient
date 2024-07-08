package sapient.model.quest

import kotlinx.serialization.Serializable

@Serializable
data class Quest(
    val id: Int = 0,
    val missionId: Int? = null,
    val parentId: Int? = null,
    val target: String = "",
    val notes: String = "",
    val completedAt: Long? = null,
    val duration: Int? = null,
)