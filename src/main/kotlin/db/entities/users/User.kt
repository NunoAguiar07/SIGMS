package isel.leic.group25.db.entities.users

import org.ktorm.entity.Entity


interface User: Entity<User> {
    val id: Int
    var email: String
    var name: String
    var password: String
    var profileImage: ByteArray
}