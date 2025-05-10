package isel.leic.group25.db.entities.rooms

import isel.leic.group25.db.entities.timetables.University
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Room: Entity<Room> {
    companion object: Entity.Factory<Room>()
    val id : Int
    var name : String
    var capacity : Int
    var university: University
}