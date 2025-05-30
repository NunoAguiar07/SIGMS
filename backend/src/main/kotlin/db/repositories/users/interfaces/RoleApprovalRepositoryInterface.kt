package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User

interface RoleApprovalRepositoryInterface {
    fun getApprovals(limit: Int, offset: Int): List<RoleApproval>
    fun getApprovalByUserId(userId: Int): RoleApproval?
    fun getApprovalByToken(token: String): RoleApproval?
    fun addPendingApproval(user: User, requestedRole: Role,
                           verificationToken: String, verifiedBy: Admin?): Boolean
    fun updateApproval(roleApproval: RoleApproval): Boolean
    fun removeApproval(roleApproval: RoleApproval): Boolean
}