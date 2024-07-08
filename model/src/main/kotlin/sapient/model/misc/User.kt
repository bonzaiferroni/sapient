package sapient.model.misc

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
)