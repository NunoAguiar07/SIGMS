package api.model.response

import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val image: String?,
    val university: String,
) {
    companion object {
        fun fromUser(user: User): UserProfileResponse {
            return UserProfileResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                image = user.profileImage?.let { Base64.getEncoder().encodeToString(it) },
                university = user.university.name,
            )
        }
    }
}