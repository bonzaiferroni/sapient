package sapient.model.quest

import kotlinx.serialization.Serializable

@Serializable
data class Quest(
    val id: Int = 0,
    val missionId: Int? = null,
    val parentId: Int? = null,
    val requireId: Int? = null,
    val target: String = "",
    val notes: String? = null,
    val completedAt: Long? = null,
    val duration: Int? = null,
) {
    val isCompleted: Boolean
        get() = completedAt != null
}