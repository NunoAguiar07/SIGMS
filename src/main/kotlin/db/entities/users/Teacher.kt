package isel.leic.group25.db.entities.users

import org.ktorm.entity.Entity

interface Teacher: Entity<Teacher> {
    val user: User
}