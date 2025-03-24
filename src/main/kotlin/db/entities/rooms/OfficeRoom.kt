package isel.leic.group25.db.entities.rooms

import org.ktorm.entity.Entity

interface OfficeRoom: Entity<OfficeRoom> {
    val room: Room
}