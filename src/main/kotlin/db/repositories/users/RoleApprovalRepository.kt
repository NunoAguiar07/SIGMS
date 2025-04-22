package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.repositories.users.interfaces.RoleApprovalRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.pendingRoleApprovals
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class RoleApprovalRepository(private val database: Database) : RoleApprovalRepositoryInterface {
    override fun getApprovals(limit: Int, offset: Int): List<RoleApproval> {
        return database.pendingRoleApprovals.drop(offset).take(limit).toList()
    }
    override fun getApprovalByUserId(userId: Int): RoleApproval? {
        return database.pendingRoleApprovals.find { it.userId eq userId }
    }
    override fun getApprovalByToken(token: String): RoleApproval? {
        return database.pendingRoleApprovals.find { it.verificationToken eq token }
    }
    override fun addPendingApproval(roleApproval: RoleApproval): Boolean {
        return database.pendingRoleApprovals.add(roleApproval) > 0
    }
    override fun updateApproval(roleApproval: RoleApproval): Boolean {
        return database.pendingRoleApprovals.update(roleApproval) > 0
    }
    override fun removeApproval(roleApproval: RoleApproval): Boolean {
        return database.pendingRoleApprovals.removeIf { it.id eq roleApproval.id } > 0
    }
}