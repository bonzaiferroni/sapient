package sapient.model.world

data class Location(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val ownerId: Int,
)