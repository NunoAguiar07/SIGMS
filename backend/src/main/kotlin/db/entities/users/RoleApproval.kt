package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
sealed interface RoleApproval : Entity<RoleApproval> {
    companion object : Entity.Factory<RoleApproval>()
    val id : Int
    var user: User
    var requestedRole: Role
    var verificationToken : String
    var verifiedBy : Admin?
    @OptIn(ExperimentalTime::class)
    var createdAt : Instant
    @OptIn(ExperimentalTime::class)
    var expiresAt : Instant
    var status : Status
}