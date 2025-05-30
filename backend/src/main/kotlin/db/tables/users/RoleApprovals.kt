package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.RoleApproval
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object RoleApprovals: Table<RoleApproval>("role_approvals") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").references(Users) { it.user }
    val role = varchar("requested_role")
        .transform({ Role.valueOf(it.uppercase()) }, { it.name.uppercase() })
        .bindTo { it.requestedRole }
    val verificationToken = varchar("verification_token").bindTo { it.verificationToken }
    val verifiedBy = int("verified_by").references(Admins) { it.verifiedBy }
    @OptIn(ExperimentalTime::class)
    val createdAt = timestamp("created_at").transform(
            { it.toKotlinInstant() },
            { it.toJavaInstant() }).bindTo { it.createdAt }
    @OptIn(ExperimentalTime::class)
    val expiresAt = timestamp("expires_at").transform(
        { it.toKotlinInstant() },
        { it.toJavaInstant() }).bindTo { it.expiresAt }
    val status = varchar("status")
        .transform({ Status.valueOf(it.uppercase()) }, { it.name.uppercase() })
        .bindTo { it.status }
}