package sapient.model.world

import kotlinx.serialization.SerialName

data class Relation(
    val id: Int,
    @SerialName("subject_id")
    val subjectId: Int,
    @SerialName("object_id")
    val objectId: Int,
    @SerialName("relation_type_id")
    val relationTypeId: Int,
    val notes: String,
)