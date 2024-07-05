package sapient.model.quest

data class Quest(
    val id: Int,
    val missionId: Int,
    val parentId: Int?,
    val target: String,
    val notes: String,
    val completedAt: Long?,
    val duration: Int,
)