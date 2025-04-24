package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class AssessRoleResponse (
    val id : Int,
    var user: UserResponse,
    var requestedRole: Role,
    var verificationToken : String,
    var verifiedBy : UserResponse?,
    var createdAt : String,
    var expiresAt : String,
    var status : Status
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun from(roleApproval: RoleApproval): AssesRoleResponse {
            val verifiedByAmin = roleApproval.verifiedBy
            val verifiedBy = if (verifiedByAmin != null) {
                UserResponse.fromUser(verifiedByAmin.user)
            } else {
                null
            }
            return AssesRoleResponse(
                id = roleApproval.id,
                user = UserResponse.fromUser(roleApproval.user),
                requestedRole = roleApproval.requestedRole,
                verificationToken = roleApproval.verificationToken,
                verifiedBy = verifiedBy,
                createdAt = roleApproval.createdAt.toString(),
                expiresAt = roleApproval.expiresAt.toString(),
                status = roleApproval.status
            )
        }
    }
}