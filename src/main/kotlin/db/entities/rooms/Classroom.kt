package isel.leic.group25.db.entities.rooms

import org.ktorm.entity.Entity

interface Classroom: Entity<Classroom> {
    val room: Room
}