package sapient.model.world

data class PersonNote(
    val id: Int,
    val personId: Int,
    val personNoteTypeId: Int,
    val note: String,
)