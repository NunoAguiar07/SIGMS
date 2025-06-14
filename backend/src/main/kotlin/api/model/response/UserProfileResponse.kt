package api.model.response

import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val image: ByteArray? = null,
    val university: String,
) {
    companion object {
        fun fromUser(user: User): UserProfileResponse {
            return UserProfileResponse(
                id = user.id,
                name = user.username,
                email = user.email,
                image = user.profileImage,
                university = user.university.name,
            )
        }
    }
}