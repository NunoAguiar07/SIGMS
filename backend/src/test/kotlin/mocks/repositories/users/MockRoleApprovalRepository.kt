package mocks.repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.RoleApprovalRepositoryInterface
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class MockRoleApprovalRepository : RoleApprovalRepositoryInterface {
    private val approvals = mutableListOf<RoleApproval>()

    override fun getApprovals(limit: Int, offset: Int): List<RoleApproval> =
        approvals.drop(offset).take(limit)

    override fun getApprovalByUserId(userId: Int): RoleApproval? =
        approvals.firstOrNull { it.user.id == userId }

    override fun getApprovalByToken(token: String): RoleApproval? =
        approvals.firstOrNull { it.verificationToken == token }

    @OptIn(ExperimentalTime::class)
    override fun addPendingApproval(user: User, requestedRole: Role,
                                    verificationToken: String, verifiedBy: Admin?): Boolean {
        val roleApproval = RoleApproval {
            this.user = user
            this.requestedRole = requestedRole
            this.verificationToken = verificationToken
            this.verifiedBy = verifiedBy
            this.createdAt = Clock.System.now()
            this.expiresAt = Clock.System.now() + 7.days
            this.status = Status.PENDING
        }
        approvals.add(roleApproval)
        return true
    }

    override fun updateApproval(roleApproval: RoleApproval): Boolean {
        val index = approvals.indexOfFirst { it.id == roleApproval.id }
        return if (index >= 0) {
            approvals[index] = roleApproval
            true
        } else {
            false
        }
    }

    override fun removeApproval(roleApproval: RoleApproval): Boolean =
        approvals.removeIf { it.id == roleApproval.id }
}
