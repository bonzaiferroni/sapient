package sapient.model.quest

import kotlinx.serialization.Serializable

@Serializable
data class Mission(
    val id: Int,
    val target: String,
)