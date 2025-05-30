package isel.leic.group25.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val userId: Int,
    val universityId: Int,
    val userRole: String
)