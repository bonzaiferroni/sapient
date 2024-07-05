package sapient.model.world

import kotlinx.serialization.SerialName

data class Need(
    val id: Int,
    @SerialName("person_id")
    val personId: Int,
    @SerialName("need_type_id")
    val needTypeId: Int,
)