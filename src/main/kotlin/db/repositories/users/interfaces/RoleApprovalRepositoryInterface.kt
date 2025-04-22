package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.users.RoleApproval

interface RoleApprovalRepositoryInterface {
    fun getApprovals(limit: Int, offset: Int): List<RoleApproval>
    fun getApprovalByUserId(userId: Int): RoleApproval?
    fun getApprovalByToken(token: String): RoleApproval?
    fun addPendingApproval(roleApproval: RoleApproval): Boolean
    fun updateApproval(roleApproval: RoleApproval): Boolean
    fun removeApproval(roleApproval: RoleApproval): Boolean
}