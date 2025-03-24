package isel.leic.group25.db.entities.users

import org.ktorm.entity.Entity

interface Admin: Entity<Admin> {
    val user: User
}