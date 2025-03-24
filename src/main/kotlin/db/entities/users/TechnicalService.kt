package isel.leic.group25.db.entities.users

import org.ktorm.entity.Entity

interface TechnicalService: Entity<TechnicalService> {
    val user: User
}