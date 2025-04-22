package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.RoleApproval
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

object RoleApprovals: Table<RoleApproval>("pending_role_approvals") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").references(Users) { it.user }
    val role = varchar("role").bindTo { it.requestedRole.toString() }
    val verificationToken = varchar("verification_token").bindTo { it.verificationToken }
    val verifiedBy = int("verified_by").references(Admins) { it.verifiedBy }
    @OptIn(ExperimentalTime::class)
    val createdAt = timestamp("created_at").bindTo { it.createdAt.toJavaInstant() }
    @OptIn(ExperimentalTime::class)
    val expiresAt = timestamp("expires_at").bindTo { it.expiresAt.toJavaInstant() }
    val status = varchar("status").bindTo { it.status.toString() }
}