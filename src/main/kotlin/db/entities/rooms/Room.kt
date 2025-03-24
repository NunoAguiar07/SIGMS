package isel.leic.group25.db.entities.rooms

import org.ktorm.entity.Entity

interface Room: Entity<Room> {
    val id : Int
    var capacity : Int
}