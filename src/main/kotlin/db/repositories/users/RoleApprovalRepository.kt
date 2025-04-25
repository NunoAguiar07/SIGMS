package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.RoleApprovalRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.pendingRoleApprovals
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class RoleApprovalRepository(private val database: Database) : RoleApprovalRepositoryInterface {
    private val tokenExpirationDays = 7

    override fun getApprovals(limit: Int, offset: Int): List<RoleApproval> {
        return database.pendingRoleApprovals.drop(offset).take(limit).toList()
    }
    override fun getApprovalByUserId(userId: Int): RoleApproval? {
        return database.pendingRoleApprovals.find { it.userId eq userId }
    }
    override fun getApprovalByToken(token: String): RoleApproval? {
        return database.pendingRoleApprovals.find { it.verificationToken eq token }
    }
    @OptIn(ExperimentalTime::class)
    override fun addPendingApproval(user: User, requestedRole: Role,
                                    verificationToken: String, verifiedBy: Admin?
    ): Boolean {
        val expiresAt = Clock.System.now() + tokenExpirationDays.days
        val roleApproval = RoleApproval {
            this.user = user
            this.requestedRole = requestedRole
            this.verificationToken = verificationToken
            this.verifiedBy = null
            this.createdAt = Clock.System.now()
            this.expiresAt = expiresAt
            this.status = Status.PENDING
        }
        return database.pendingRoleApprovals.add(roleApproval) > 0
    }
    override fun updateApproval(roleApproval: RoleApproval): Boolean {
        return database.pendingRoleApprovals.update(roleApproval) > 0
    }
    override fun removeApproval(roleApproval: RoleApproval): Boolean {
        return database.pendingRoleApprovals.removeIf { it.id eq roleApproval.id } > 0
    }
}