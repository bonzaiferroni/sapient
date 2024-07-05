package sapient.model.world

import kotlinx.serialization.SerialName

data class Person(
    val id: Int,
    val name: String,
    val birthday: Long?,
    val phone: String?,
    @SerialName("img_url")
    val imgUrl: String?,
)